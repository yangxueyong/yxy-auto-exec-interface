package com.yxy.auto.service;

import com.yxy.auto.dto.ApiTestRecordDTO;
import com.yxy.auto.vo.ApiTestResultVO;

import java.util.List;

public interface ApiTestService {
    List<ApiTestRecordDTO> listAll();
    ApiTestRecordDTO getById(Long id);
    ApiTestRecordDTO create(ApiTestRecordDTO dto);
    void startTest(Long id);
    ApiTestResultVO getTestResult(Long id);
    void delete(Long id);
}