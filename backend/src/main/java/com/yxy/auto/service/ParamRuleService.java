package com.yxy.auto.service;

import com.yxy.auto.dto.ParamRuleDTO;

import java.util.List;

public interface ParamRuleService {
    /**
     * 查询所有入参规则
     */
    List<ParamRuleDTO> listAll();
    
    /**
     * 根据ID查询入参规则
     */
    ParamRuleDTO getById(Long id);
    
    /**
     * 创建入参规则
     */
    ParamRuleDTO create(ParamRuleDTO dto);
    
    /**
     * 更新入参规则
     */
    ParamRuleDTO update(ParamRuleDTO dto);
    
    /**
     * 删除入参规则
     */
    void delete(Long id);
}