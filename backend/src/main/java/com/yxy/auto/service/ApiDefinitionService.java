package com.yxy.auto.service;

import com.yxy.auto.dto.ApiDefinitionDTO;

import java.util.List;

public interface ApiDefinitionService {
    List<ApiDefinitionDTO> listAll();
    ApiDefinitionDTO getById(Long id);
    ApiDefinitionDTO create(ApiDefinitionDTO dto);
    ApiDefinitionDTO update(ApiDefinitionDTO dto);
    void delete(Long id);
}