package com.yxy.auto.controller;

import com.yxy.auto.dto.ResponseRuleDTO;
import com.yxy.auto.service.ResponseRuleService;
import com.yxy.auto.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/response-rules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ResponseRuleController {
    
    private final ResponseRuleService responseRuleService;
    
    @GetMapping
    public ApiResponse<List<ResponseRuleDTO>> list() {
        return ApiResponse.success(responseRuleService.listAll());
    }
    
    @GetMapping("/{id}")
    public ApiResponse<ResponseRuleDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(responseRuleService.getById(id));
    }
    
    @PostMapping
    public ApiResponse<ResponseRuleDTO> create(@RequestBody ResponseRuleDTO dto) {
        return ApiResponse.success(responseRuleService.create(dto));
    }
    
    @PutMapping
    public ApiResponse<ResponseRuleDTO> update(@RequestBody ResponseRuleDTO dto) {
        return ApiResponse.success(responseRuleService.update(dto));
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        responseRuleService.delete(id);
        return ApiResponse.success(null);
    }
}