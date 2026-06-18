package com.yxy.auto.controller;

import com.yxy.auto.dto.ApiDefinitionDTO;
import com.yxy.auto.service.ApiDefinitionService;
import com.yxy.auto.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/api-definitions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ApiDefinitionController {
    
    private final ApiDefinitionService apiDefinitionService;
    
    @GetMapping
    public ApiResponse<List<ApiDefinitionDTO>> list() {
        return ApiResponse.success(apiDefinitionService.listAll());
    }
    
    @GetMapping("/{id}")
    public ApiResponse<ApiDefinitionDTO> getById(@PathVariable Long id) {
        return ApiResponse.success(apiDefinitionService.getById(id));
    }
    
    @PostMapping
    public ApiResponse<ApiDefinitionDTO> create(@RequestBody ApiDefinitionDTO dto) {
        return ApiResponse.success(apiDefinitionService.create(dto));
    }
    
    @PutMapping
    public ApiResponse<ApiDefinitionDTO> update(@RequestBody ApiDefinitionDTO dto) {
        return ApiResponse.success(apiDefinitionService.update(dto));
    }
    
    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        apiDefinitionService.delete(id);
        return ApiResponse.success(null);
    }
}