package com.yxy.auto.controller;

import com.yxy.auto.dto.SqlRuleDTO;
import com.yxy.auto.service.SqlRuleService;
import com.yxy.auto.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sql-rules")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SqlRuleController {
    
    private final SqlRuleService sqlRuleService;
    
    @GetMapping
    public ApiResponse<List<SqlRuleDTO>> list() {
        return ApiResponse.success(sqlRuleService.listAll());
    }
    
    @GetMapping("/{id}")
    public ApiResponse<SqlRuleDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(sqlRuleService.getById(id));
    }
    
    @PostMapping
    public ApiResponse<SqlRuleDTO> create(@RequestBody SqlRuleDTO dto) {
        return ApiResponse.success(sqlRuleService.create(dto));
    }
    
    @PutMapping
    public ApiResponse<SqlRuleDTO> update(@RequestBody SqlRuleDTO dto) {
        return ApiResponse.success(sqlRuleService.update(dto));
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sqlRuleService.delete(id);
        return ApiResponse.success(null);
    }
}