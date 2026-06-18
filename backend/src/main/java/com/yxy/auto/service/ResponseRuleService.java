package com.yxy.auto.service;

import com.yxy.auto.dto.ResponseRuleDTO;

import java.util.List;

public interface ResponseRuleService {
    List<ResponseRuleDTO> listAll();
    ResponseRuleDTO getById(Long id);
    ResponseRuleDTO create(ResponseRuleDTO dto);
    ResponseRuleDTO update(ResponseRuleDTO dto);
    void delete(Long id);
}