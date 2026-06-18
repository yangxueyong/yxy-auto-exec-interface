package com.yxy.auto.controller;

import com.yxy.auto.dto.ParamRuleDTO;
import com.yxy.auto.service.ParamRuleService;
import com.yxy.auto.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/param-rules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ParamRuleController {
    
    private final ParamRuleService paramRuleService;
    
    @GetMapping
    public ApiResponse<List<ParamRuleDTO>> list() {
        return ApiResponse.success(paramRuleService.listAll());
    }
    
    @GetMapping("/{id}")
    public ApiResponse<ParamRuleDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(paramRuleService.getById(id));
    }
    
    @PostMapping
    public ApiResponse<ParamRuleDTO> create(@RequestBody ParamRuleDTO dto) {
        return ApiResponse.success(paramRuleService.create(dto));
    }
    
    @PutMapping
    public ApiResponse<ParamRuleDTO> update(@RequestBody ParamRuleDTO dto) {
        return ApiResponse.success(paramRuleService.update(dto));
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        paramRuleService.delete(id);
        return ApiResponse.success(null);
    }
}