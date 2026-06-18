package com.yxy.auto.service;

import com.yxy.auto.dto.SqlRuleDTO;

import java.util.List;

public interface SqlRuleService {
    List<SqlRuleDTO> listAll();
    SqlRuleDTO create(SqlRuleDTO dto);
    SqlRuleDTO update(SqlRuleDTO dto);
    void delete(Long id);
    SqlRuleDTO getById(Long id);
}