package com.yxy.auto.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yxy.auto.dto.SqlRuleDTO;
import com.yxy.auto.entity.SqlRule;
import com.yxy.auto.mapper.SqlRuleMapper;
import com.yxy.auto.service.SqlRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SqlRuleServiceImpl implements SqlRuleService {
    
    private final SqlRuleMapper sqlRuleMapper;
    
    @Override
    public List<SqlRuleDTO> listAll() {
        LambdaQueryWrapper<SqlRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(SqlRule::getCreatedTime);
        List<SqlRule> rules = sqlRuleMapper.selectList(wrapper);
        return rules.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public SqlRuleDTO create(SqlRuleDTO dto) {
        // 解析SQL中的字段
        String fields = parseSqlFields(dto.getRuleSql());
        dto.setRuleFields(fields);
        
        SqlRule entity = new SqlRule();
        BeanUtil.copyProperties(dto, entity);
        sqlRuleMapper.insert(entity);
        dto.setId(entity.getId());
        dto.setFieldList(Arrays.asList(fields.split(",")));
        return dto;
    }
    
    @Override
    @Transactional
    public SqlRuleDTO update(SqlRuleDTO dto) {
        String fields = parseSqlFields(dto.getRuleSql());
        dto.setRuleFields(fields);
        
        SqlRule entity = new SqlRule();
        BeanUtil.copyProperties(dto, entity);
        sqlRuleMapper.updateById(entity);
        dto.setFieldList(Arrays.asList(fields.split(",")));
        return dto;
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        sqlRuleMapper.deleteById(id);
    }
    
    @Override
    public SqlRuleDTO getById(Long id) {
        SqlRule entity = sqlRuleMapper.selectById(id);
        return toDTO(entity);
    }
    
    /**
     * 解析SQL中的查询字段
     */
    private String parseSqlFields(String sql) {
        // 简单的SQL解析，实际项目可能需要更复杂的解析
        String upperSql = sql.toUpperCase();
        int selectIndex = upperSql.indexOf("SELECT");
        int fromIndex = upperSql.indexOf("FROM");
        if (selectIndex == -1 || fromIndex == -1) {
            throw new RuntimeException("Invalid SQL: " + sql);
        }
        String fieldStr = sql.substring(selectIndex + 6, fromIndex).trim();
        // 处理可能的空格和换行
        fieldStr = fieldStr.replaceAll("\\s+", " ");
        // 提取字段名
        String[] fields = fieldStr.split(",");
        List<String> fieldList = Arrays.stream(fields)
                .map(f -> {
                    String trimmed = f.trim();
                    // 处理 as 别名
                    int asIndex = trimmed.toUpperCase().indexOf(" AS ");
                    if (asIndex != -1) {
                        return trimmed.substring(asIndex + 4).trim();
                    }
                    // 处理表名.字段
                    int dotIndex = trimmed.indexOf(".");
                    if (dotIndex != -1) {
                        return trimmed.substring(dotIndex + 1).trim();
                    }
                    return trimmed;
                })
                .collect(Collectors.toList());
        return String.join(",", fieldList);
    }
    
    private SqlRuleDTO toDTO(SqlRule entity) {
        if (entity == null) return null;
        SqlRuleDTO dto = new SqlRuleDTO();
        BeanUtil.copyProperties(entity, dto);
        if (StrUtil.isNotBlank(entity.getRuleFields())) {
            dto.setFieldList(Arrays.asList(entity.getRuleFields().split(",")));
        }
        return dto;
    }
}