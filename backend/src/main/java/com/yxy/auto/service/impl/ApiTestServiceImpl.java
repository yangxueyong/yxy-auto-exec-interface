package com.yxy.auto.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.yxy.auto.dto.ApiTestRecordDTO;
import com.yxy.auto.dto.ParamRuleDTO;
import com.yxy.auto.entity.*;
import com.yxy.auto.mapper.*;
import com.yxy.auto.service.ApiTestService;
import com.yxy.auto.service.DynamicSqlService;
import com.yxy.auto.vo.ApiTestResultVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiTestServiceImpl implements ApiTestService {
    
    private final ApiTestRecordMapper apiTestRecordMapper;
    private final ApiDefinitionMapper apiDefinitionMapper;
    private final ParamRuleMapper paramRuleMapper;
    private final ResponseRuleMapper responseRuleMapper;
    private final SqlRuleMapper sqlRuleMapper;
    private final RestTemplate restTemplate;
    private final DynamicSqlService dynamicSqlService;
    
    @Value("${test.http.connect-timeout:5000}")
    private int connectTimeout;
    
    @Value("${test.http.read-timeout:10000}")
    private int readTimeout;
    
    @Override
    public List<ApiTestRecordDTO> listAll() {
        LambdaQueryWrapper<ApiTestRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ApiTestRecord::getCreatedTime);
        List<ApiTestRecord> entities = apiTestRecordMapper.selectList(wrapper);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public ApiTestRecordDTO getById(Long id) {
        ApiTestRecord entity = apiTestRecordMapper.selectById(id);
        return toDTO(entity);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiTestRecordDTO create(ApiTestRecordDTO dto) {
        ApiTestRecord entity = new ApiTestRecord();
        BeanUtil.copyProperties(dto, entity);
        entity.setStatus(0); // 未执行
        apiTestRecordMapper.insert(entity);
        dto.setId(entity.getId());
        return dto;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void startTest(Long id) {
        ApiTestRecord record = apiTestRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("测试记录不存在");
        }
        if (record.getStatus() == 1) {
            throw new RuntimeException("测试正在执行中");
        }
        
        // 更新状态为执行中
        record.setStatus(1);
        apiTestRecordMapper.updateById(record);
        
        // 异步执行测试
        CompletableFuture.runAsync(() -> executeTest(record));
    }
    
    /**
     * 执行测试
     */
    private void executeTest(ApiTestRecord record) {
        try {
            // 获取接口定义
            ApiDefinition apiDefinition = apiDefinitionMapper.selectById(record.getApiDefinitionId());
            if (apiDefinition == null) {
                throw new RuntimeException("接口定义不存在");
            }
            
            // 获取入参规则
            ParamRule paramRule = paramRuleMapper.selectById(apiDefinition.getParamRuleId());
            if (paramRule == null) {
                throw new RuntimeException("入参规则不存在");
            }
            
            // 获取返参规则列表
            List<ResponseRule> responseRules = new ArrayList<>();
            if (StrUtil.isNotBlank(apiDefinition.getResponseRuleIds())) {
                List<Long> ids = Arrays.stream(apiDefinition.getResponseRuleIds().split(","))
                        .map(Long::parseLong)
                        .collect(Collectors.toList());
                responseRules = responseRuleMapper.selectBatchIds(ids);
            }
            
            // 执行测试
            int totalCount = record.getTotalCount();
            int threadCount = record.getThreadCount();
            int duration = record.getTestDuration();
            
            // 使用线程池并发执行
            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            List<Future<TestResult>> futures = new ArrayList<>();
            CountDownLatch latch = new CountDownLatch(totalCount);
            
            long startTime = System.currentTimeMillis();
            long endTime = startTime + duration * 1000L;
            
            // 生成测试数据
            List<JSONObject> testDataList = generateTestData(paramRule, totalCount);
            
            // 提交任务
            for (int i = 0; i < totalCount; i++) {
//                final JSONObject requestData = testDataList.get(i % testDataList.size());
                List<ResponseRule> finalResponseRules = responseRules;
                Future<TestResult> future = executor.submit(() -> {
                    try {
                        JSONObject requestData = generateTestData(paramRule);
                        return executeSingleTest(apiDefinition, requestData, finalResponseRules);
                    } finally {
                        latch.countDown();
                    }
                });
                futures.add(future);
                
                // 检查时间限制
                if (System.currentTimeMillis() >= endTime) {
                    break;
                }
            }
            
            // 等待所有任务完成或超时
            try {
                long remaining = endTime - System.currentTimeMillis();
                if (remaining > 0) {
                    latch.await(remaining, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            // 关闭线程池
            executor.shutdown();
            try {
                if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
            
            // 收集结果
            List<TestResult> results = new ArrayList<>();
            for (Future<TestResult> future : futures) {
                try {
                    if (future.isDone() && !future.isCancelled()) {
                        results.add(future.get());
                    }
                } catch (Exception e) {
                    log.error("获取测试结果失败: {}", e.getMessage());
                }
            }
            
            // 统计结果
            TestStatistics statistics = calculateStatistics(results);
            
            // 更新测试记录
            record.setTotalCount(results.size());
            record.setSuccessCount(statistics.successCount);
            record.setFailCount(statistics.failCount);
            record.setMaxTime(statistics.maxTime);
            record.setMinTime(statistics.minTime);
            record.setAvgTime(BigDecimal.valueOf(statistics.avgTime).setScale(2, RoundingMode.HALF_UP));
            record.setStatus(2); // 已完成
            record.setResultDetail(JSON.toJSONString(statistics));
            apiTestRecordMapper.updateById(record);
            
            log.info("测试完成: id={}, 总次数={}, 成功={}, 失败={}", 
                record.getId(), results.size(), statistics.successCount, statistics.failCount);
            
        } catch (Exception e) {
            log.error("执行测试失败: {}", e.getMessage(), e);
            // 更新测试记录状态为失败
            record.setStatus(2);
            apiTestRecordMapper.updateById(record);
        }
    }
    
    /**
     * 执行单次测试
     */
    private TestResult executeSingleTest(ApiDefinition apiDefinition, JSONObject requestData, 
                                          List<ResponseRule> responseRules) {
        long startTime = System.nanoTime();
        TestResult result = new TestResult();
        
        try {
            // 发送HTTP请求
            String url = apiDefinition.getApiUrl();
            String method = apiDefinition.getApiMethod();
            
            String response;
            if ("GET".equalsIgnoreCase(method)) {
                // GET请求，将参数拼接到URL
                String queryString = buildQueryString(requestData);
                String fullUrl = url + (queryString.isEmpty() ? "" : "?" + queryString);
                response = restTemplate.getForObject(fullUrl, String.class);
            } else {
                // POST/PUT/DELETE请求，发送JSON body
                response = restTemplate.postForObject(url, requestData, String.class);
            }
            
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // 毫秒
            
            result.duration = duration;
            result.response = response;
            
            // 验证响应
            boolean success = validateResponse(response, responseRules);
            result.success = success;
            
        } catch (Exception e) {
            log.error("单次测试失败: {}", e.getMessage());
            long endTime = System.nanoTime();
            result.duration = (endTime - startTime) / 1_000_000;
            result.success = false;
            result.error = e.getMessage();
        }
        
        return result;
    }
    
    /**
     * 生成测试数据
     */
    private List<JSONObject> generateTestData(ParamRule paramRule, int count) {
        List<JSONObject> dataList = new ArrayList<>();
        
        // 解析参数配置
        List<ParamRuleDTO.ParamConfig> configs = JSON.parseArray(
            paramRule.getParamConfig(), 
            ParamRuleDTO.ParamConfig.class
        );
        
        for (int i = 0; i < count; i++) {
            JSONObject data = new JSONObject();
            for (ParamRuleDTO.ParamConfig config : configs) {
                Object value = generateParamValue(config);
                setNestedValue(data, config.getParamName(), value);
            }
            dataList.add(data);
        }
        
        return dataList;
    }

    /**
     * 生成测试数据
     */
    private JSONObject generateTestData(ParamRule paramRule) {
//        List<JSONObject> dataList = new ArrayList<>();

        // 解析参数配置
        List<ParamRuleDTO.ParamConfig> configs = JSON.parseArray(
                paramRule.getParamConfig(),
                ParamRuleDTO.ParamConfig.class
        );

//        for (int i = 0; i < count; i++) {
            JSONObject data = new JSONObject();
            for (ParamRuleDTO.ParamConfig config : configs) {
                Object value = generateParamValue(config);
                setNestedValue(data, config.getParamName(), value);
            }
//            dataList.add(data);
//        }

        return data;
    }
    
    /**
     * 生成参数值
     */
    private Object generateParamValue(ParamRuleDTO.ParamConfig config) {
        String type = config.getParamType();
        
        if (type == null || "fixed".equals(type)) {
            return config.getFixedValue();
        }
        
        if ("db".equals(type)) {
            // 从数据库查询
            return queryFromDatabase(config);
        }
        
        if ("random_time".equals(type)) {
            // 随机时间
            return DateUtil.format(LocalDateTime.now().plusSeconds(new Random().nextInt(86400)), 
                "yyyy-MM-dd HH:mm:ss");
        }
        
        if ("random_date".equals(type)) {
            // 随机日期
            return DateUtil.format(LocalDateTime.now().plusDays(new Random().nextInt(365)), 
                "yyyy-MM-dd");
        }
        
        if ("manual".equals(type)) {
            // 手动数据集随机选择
            List<String> values = config.getManualValues();
            if (values != null && !values.isEmpty()) {
                int index = new Random().nextInt(values.size());
                return values.get(index);
            }
        }
        
        return null;
    }
    
    /**
     * 从数据库查询值
     */
    private Object queryFromDatabase(ParamRuleDTO.ParamConfig config) {
        if (config.getSqlRuleId() == null || StrUtil.isBlank(config.getSqlField())) {
            return null;
        }
        
        try {
            // 获取SQL规则
            SqlRule sqlRule = sqlRuleMapper.selectById(config.getSqlRuleId());
            if (sqlRule == null) {
                return null;
            }

            Map<String, Object> stringObjectMap = dynamicSqlService.executeSql(sqlRule.getRuleSql());
            if(CollUtil.isEmpty(stringObjectMap)){
                return null;
            }

            return stringObjectMap.get(config.getSqlField());
            // 执行SQL查询
            // 注意：这里需要配置数据源并执行SQL，实际项目中需要实现
            // 这里简化为返回模拟数据
//            return "value_from_db_" + System.currentTimeMillis();
            
        } catch (Exception e) {
            log.error("从数据库查询失败: ", e);
            return null;
        }
    }
    
    /**
     * 设置嵌套值
     */
    private void setNestedValue(JSONObject obj, String path, Object value) {
        String[] parts = path.split("\\.");
        JSONObject current = obj;
        for (int i = 0; i < parts.length - 1; i++) {
            String part = parts[i];
            // 处理数组索引
            if (part.contains("[")) {
                // 简化处理，实际项目中需要实现
                String key = part.substring(0, part.indexOf("["));
                JSONObject nested = current.getJSONObject(key);
                if (nested == null) {
                    nested = new JSONObject();
                    current.put(key, nested);
                }
                current = nested;
            } else {
                if (!current.containsKey(part)) {
                    current.put(part, new JSONObject());
                }
                current = current.getJSONObject(part);
            }
        }
        String lastKey = parts[parts.length - 1];
        // 处理数组索引
        if (lastKey.contains("[")) {
            // 简化处理
            String key = lastKey.substring(0, lastKey.indexOf("["));
            current.put(key, value);
        } else {
            current.put(lastKey, value);
        }
    }
    
    /**
     * 构建查询字符串
     */
    private String buildQueryString(JSONObject data) {
        List<String> pairs = new ArrayList<>();
        for (String key : data.keySet()) {
            Object value = data.get(key);
            if (value != null) {
                pairs.add(key + "=" + value.toString());
            }
        }
        return String.join("&", pairs);
    }
    
    /**
     * 验证响应
     */
    private boolean validateResponse(String response, List<ResponseRule> responseRules) {
        if (responseRules.isEmpty()) {
            return true;
        }
        
        try {
            // 解析响应为JSON
            Map responseMap = JSON.parseObject(response, Map.class);
            
            // 创建SPEL上下文
            StandardEvaluationContext context = new StandardEvaluationContext();
            context.setVariable("result", responseMap);
            
            ExpressionParser parser = new SpelExpressionParser();
            
            // 所有规则都必须通过
            for (ResponseRule rule : responseRules) {
                try {
                    Expression expression = parser.parseExpression(rule.getSpelExpression());
                    Boolean value = expression.getValue(context, Boolean.class);
                    if (value == null || !value) {
                        return false;
                    }
                } catch (Exception e) {
                    log.error("SPEL表达式执行失败: {}", rule.getSpelExpression(), e);
                    return false;
                }
            }
            return true;
            
        } catch (Exception e) {
            log.error("验证响应失败: {}", e.getMessage());
            return false;
        }
    }

    public static void main(String[] args) {
        Map responseMap = new HashMap();
        responseMap.put("code", 200);

        // 创建SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext(responseMap);
        context.setVariable("result", responseMap);

        ExpressionParser parser = new SpelExpressionParser();
        Expression expression = parser.parseExpression("#result['code'] == 200");
        Boolean value = expression.getValue(context, Boolean.class);
        System.out.println(value);


        // 创建表达式解析器
        parser = new SpelExpressionParser();

        // 创建评估上下文
        context = new StandardEvaluationContext();

        // 设置变量
        context.setVariable("name", "张三");
        context.setVariable("age", 25);
        context.setVariable("salary", 10000.0);

        // 1. 字符串拼接
        Expression exp1 = parser.parseExpression("#name + '的年龄是' + #age + '岁'");
        String result1 = exp1.getValue(context, String.class);
        System.out.println(result1); // 张三的年龄是25岁

        // 2. 算术运算
        Expression exp2 = parser.parseExpression("#age * 12");
        Integer result2 = exp2.getValue(context, Integer.class);
        System.out.println("年龄的月数: " + result2); // 300

        // 3. 条件判断
        Expression exp3 = parser.parseExpression("#age >= 18 ? '成年人' : '未成年人'");
        String result3 = exp3.getValue(context, String.class);
        System.out.println(result3); // 成年人

        // 4. 逻辑运算
        Expression exp4 = parser.parseExpression("#age == 25 and #salary > 5000");
        Boolean result4 = exp4.getValue(context, Boolean.class);
        System.out.println("是否符合条件: " + result4); // true
    }
    
    /**
     * 计算统计信息
     */
    private TestStatistics calculateStatistics(List<TestResult> results) {
        TestStatistics stats = new TestStatistics();
        stats.totalCount = results.size();
        stats.successCount = (int) results.stream().filter(r -> r.success).count();
        stats.failCount = stats.totalCount - stats.successCount;
        
        List<Long> durations = results.stream()
                .map(r -> r.duration)
                .filter(d -> d > 0)
                .collect(Collectors.toList());
        
        if (!durations.isEmpty()) {
            stats.maxTime = durations.stream().max(Long::compare).orElse(0L);
            stats.minTime = durations.stream().min(Long::compare).orElse(0L);
            stats.avgTime = durations.stream().mapToLong(Long::longValue).average().orElse(0);
        }
        
        return stats;
    }
    
    @Override
    public ApiTestResultVO getTestResult(Long id) {
        ApiTestRecord record = apiTestRecordMapper.selectById(id);
        if (record == null) {
            throw new RuntimeException("测试记录不存在");
        }
        
        ApiTestResultVO result = new ApiTestResultVO();
        result.setTotalCount(record.getTotalCount());
        result.setSuccessCount(record.getSuccessCount());
        result.setFailCount(record.getFailCount());
        result.setMaxTime(record.getMaxTime());
        result.setMinTime(record.getMinTime());
        result.setAvgTime(record.getAvgTime() != null ? record.getAvgTime().doubleValue() : 0.0);
        result.setStatus(record.getStatus() == 2 ? "已完成" : "未完成");
        
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        apiTestRecordMapper.deleteById(id);
    }
    
    private ApiTestRecordDTO toDTO(ApiTestRecord entity) {
        if (entity == null) {
            return null;
        }
        ApiTestRecordDTO dto = new ApiTestRecordDTO();
        BeanUtil.copyProperties(entity, dto);
        
        // 查询接口名称 - 修正：直接调用selectById，检查返回值
        if (entity.getApiDefinitionId() != null) {
            ApiDefinition apiDefinition = apiDefinitionMapper.selectById(entity.getApiDefinitionId());
            if (apiDefinition != null) {
                dto.setApiName(apiDefinition.getApiName());
            }
        }
        
        return dto;
    }
    
    /**
     * 内部类：测试结果
     */
    @lombok.Data
    private static class TestResult {
        private boolean success;
        private long duration;
        private String response;
        private String error;
    }
    
    /**
     * 内部类：测试统计
     */
    @lombok.Data
    private static class TestStatistics {
        private int totalCount;
        private int successCount;
        private int failCount;
        private long maxTime;
        private long minTime;
        private double avgTime;
    }
}