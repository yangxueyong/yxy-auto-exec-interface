package com.yxy.auto.controller;

import com.yxy.auto.dto.ApiTestRecordDTO;
import com.yxy.auto.service.ApiTestService;
import com.yxy.auto.vo.ApiResponse;
import com.yxy.auto.vo.ApiTestResultVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/api-tests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApiTestController {
    
    private final ApiTestService apiTestService;
    
    @GetMapping
    public ApiResponse<List<ApiTestRecordDTO>> list() {
        return ApiResponse.success(apiTestService.listAll());
    }
    
    @GetMapping("/{id}")
    public ApiResponse<ApiTestRecordDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(apiTestService.getById(id));
    }
    
    @GetMapping("/{id}/result")
    public ApiResponse<ApiTestResultVO> getResult(@PathVariable Long id) {
        return ApiResponse.success(apiTestService.getTestResult(id));
    }
    
    @PostMapping
    public ApiResponse<ApiTestRecordDTO> create(@RequestBody ApiTestRecordDTO dto) {
        return ApiResponse.success(apiTestService.create(dto));
    }
    
    @PostMapping("/{id}/start")
    public ApiResponse<Void> startTest(@PathVariable Long id) {
        apiTestService.startTest(id);
        return ApiResponse.success(null);
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        apiTestService.delete(id);
        return ApiResponse.success(null);
    }
}