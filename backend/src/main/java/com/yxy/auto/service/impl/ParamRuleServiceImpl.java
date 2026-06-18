package com.yxy.auto.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.yxy.auto.dto.ParamRuleDTO;
import com.yxy.auto.entity.ParamRule;
import com.yxy.auto.mapper.ParamRuleMapper;
import com.yxy.auto.service.ParamRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ParamRuleServiceImpl implements ParamRuleService {
    
    private final ParamRuleMapper paramRuleMapper;
    
    @Override
    public List<ParamRuleDTO> listAll() {
        LambdaQueryWrapper<ParamRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ParamRule::getCreatedTime);
        return paramRuleMapper.selectList(wrapper).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public ParamRuleDTO create(ParamRuleDTO dto) {
        // 解析JSON参数
        parseParamJson(dto);
        
        ParamRule entity = new ParamRule();
        entity.setRuleName(dto.getRuleName());
        entity.setParamJson(dto.getParamJson());
        entity.setParamConfig(JSON.toJSONString(dto.getParamConfigs()));
        paramRuleMapper.insert(entity);
        dto.setId(entity.getId());
        return dto;
    }
    
    @Override
    @Transactional
    public ParamRuleDTO update(ParamRuleDTO dto) {
        parseParamJson(dto);
        
        ParamRule entity = new ParamRule();
        entity.setId(dto.getId());
        entity.setRuleName(dto.getRuleName());
        entity.setParamJson(dto.getParamJson());
        entity.setParamConfig(JSON.toJSONString(dto.getParamConfigs()));
        paramRuleMapper.updateById(entity);
        return dto;
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        paramRuleMapper.deleteById(id);
    }
    
    @Override
    public ParamRuleDTO getById(Long id) {
        ParamRule entity = paramRuleMapper.selectById(id);
        return toDTO(entity);
    }
    
    private void parseParamJson(ParamRuleDTO dto) {
        String paramJson = dto.getParamJson();
        // 使用fastjson解析JSON，提取所有字段
        // 这里简化处理，实际可以使用JSONPath
        // 假设paramJson是一个JSON对象
        List<ParamRuleDTO.ParamConfig> configs = dto.getParamConfigs();
        if (configs == null) {
            // 从JSON中提取字段名
            // 这里简化处理，实际需要递归解析
        }
    }
    
    private ParamRuleDTO toDTO(ParamRule entity) {
        if (entity == null) return null;
        ParamRuleDTO dto = new ParamRuleDTO();
        dto.setId(entity.getId());
        dto.setRuleName(entity.getRuleName());
        dto.setParamJson(entity.getParamJson());
        if (entity.getParamConfig() != null) {
            dto.setParamConfigs(JSON.parseArray(entity.getParamConfig(), ParamRuleDTO.ParamConfig.class));
        }
        return dto;
    }
}