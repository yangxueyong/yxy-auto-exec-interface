package com.yxy.auto.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.bean.BeanUtil;
import com.yxy.auto.dto.ResponseRuleDTO;
import com.yxy.auto.entity.ResponseRule;
import com.yxy.auto.mapper.ResponseRuleMapper;
import com.yxy.auto.service.ResponseRuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResponseRuleServiceImpl implements ResponseRuleService {
    
    private final ResponseRuleMapper responseRuleMapper;
    
    @Override
    public List<ResponseRuleDTO> listAll() {
        LambdaQueryWrapper<ResponseRule> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(ResponseRule::getCreatedTime);
        return responseRuleMapper.selectList(wrapper).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public ResponseRuleDTO getById(Long id) {
        ResponseRule entity = responseRuleMapper.selectById(id);
        return toDTO(entity);
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseRuleDTO create(ResponseRuleDTO dto) {
        ResponseRule entity = new ResponseRule();
        BeanUtil.copyProperties(dto, entity);
        responseRuleMapper.insert(entity);
        dto.setId(entity.getId());
        return dto;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseRuleDTO update(ResponseRuleDTO dto) {
        ResponseRule entity = new ResponseRule();
        BeanUtil.copyProperties(dto, entity);
        responseRuleMapper.updateById(entity);
        return dto;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        responseRuleMapper.deleteById(id);
    }
    
    private ResponseRuleDTO toDTO(ResponseRule entity) {
        if (entity == null) {
            return null;
        }
        ResponseRuleDTO dto = new ResponseRuleDTO();
        BeanUtil.copyProperties(entity, dto);
        return dto;
    }
}