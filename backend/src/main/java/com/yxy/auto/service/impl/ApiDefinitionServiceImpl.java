package com.yxy.auto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.yxy.auto.dto.ApiDefinitionDTO;
import com.yxy.auto.entity.ApiDefinition;
import com.yxy.auto.entity.ParamRule;
import com.yxy.auto.entity.ResponseRule;
import com.yxy.auto.mapper.ApiDefinitionMapper;
import com.yxy.auto.mapper.ParamRuleMapper;
import com.yxy.auto.mapper.ResponseRuleMapper;
import com.yxy.auto.service.ApiDefinitionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApiDefinitionServiceImpl implements ApiDefinitionService {
    
    private final ApiDefinitionMapper apiDefinitionMapper;
    private final ParamRuleMapper paramRuleMapper;
    private final ResponseRuleMapper responseRuleMapper;
    
    @Override
    public List<ApiDefinitionDTO> listAll() {
        LambdaQueryWrapper<ApiDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ApiDefinition::getCreatedTime);
        List<ApiDefinition> entities = apiDefinitionMapper.selectList(wrapper);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public ApiDefinitionDTO getById(Long id) {
        ApiDefinition entity = apiDefinitionMapper.selectById(id);
        return toDTO(entity);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiDefinitionDTO create(ApiDefinitionDTO dto) {
        ApiDefinition entity = new ApiDefinition();
        BeanUtil.copyProperties(dto, entity);
        // 处理responseRuleIds
        if (dto.getResponseRuleIds() != null && !dto.getResponseRuleIds().isEmpty()) {
            entity.setResponseRuleIds(dto.getResponseRuleIds());
        }
        apiDefinitionMapper.insert(entity);
        dto.setId(entity.getId());
        return enrichDTO(dto);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiDefinitionDTO update(ApiDefinitionDTO dto) {
        ApiDefinition entity = new ApiDefinition();
        BeanUtil.copyProperties(dto, entity);
        if (dto.getResponseRuleIds() != null && !dto.getResponseRuleIds().isEmpty()) {
            entity.setResponseRuleIds(dto.getResponseRuleIds());
        }
        apiDefinitionMapper.updateById(entity);
        return enrichDTO(dto);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        apiDefinitionMapper.deleteById(id);
    }
    
    private ApiDefinitionDTO toDTO(ApiDefinition entity) {
        if (entity == null) {
            return null;
        }
        ApiDefinitionDTO dto = new ApiDefinitionDTO();
        BeanUtil.copyProperties(entity, dto);
        
        // 查询入参规则名称
        if (entity.getParamRuleId() != null) {
            // 修正：直接调用selectById，检查返回值
            ParamRule paramRule = paramRuleMapper.selectById(entity.getParamRuleId());
            if (paramRule != null) {
                dto.setParamRuleName(paramRule.getRuleName());
            }
        }
        
        // 查询返参规则名称
        if (StrUtil.isNotBlank(entity.getResponseRuleIds())) {
            List<Long> ids = Arrays.stream(entity.getResponseRuleIds().split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<ResponseRule> rules = responseRuleMapper.selectBatchIds(ids);
            List<String> names = rules.stream()
                    .map(ResponseRule::getRuleName)
                    .collect(Collectors.toList());
            dto.setResponseRuleNames(String.join(",", names));
        }
        
        return dto;
    }
    
    private ApiDefinitionDTO enrichDTO(ApiDefinitionDTO dto) {
        // 补充名称信息
        if (dto.getParamRuleId() != null) {
            // 修正：直接调用selectById，检查返回值
            ParamRule paramRule = paramRuleMapper.selectById(dto.getParamRuleId());
            if (paramRule != null) {
                dto.setParamRuleName(paramRule.getRuleName());
            }
        }
        if (StrUtil.isNotBlank(dto.getResponseRuleIds())) {
            List<Long> ids = Arrays.stream(dto.getResponseRuleIds().split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
            List<ResponseRule> rules = responseRuleMapper.selectBatchIds(ids);
            List<String> names = rules.stream()
                    .map(ResponseRule::getRuleName)
                    .collect(Collectors.toList());
            dto.setResponseRuleNames(String.join(",", names));
        }
        return dto;
    }
}