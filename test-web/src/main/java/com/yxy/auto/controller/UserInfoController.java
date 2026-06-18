package com.yxy.auto.controller;

import com.yxy.auto.dto.UserInfoDTO;
import com.yxy.auto.io.QueryUserInfoIO;
import com.yxy.auto.service.UserInfoService;
import com.yxy.auto.vo.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/api-user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserInfoController {
    
    private final UserInfoService userInfoService;
    
    @PostMapping("/getByIo")
    public ApiResponse<List<UserInfoDTO>> getByIo(@RequestBody QueryUserInfoIO io) {
        return ApiResponse.success(userInfoService.getByIo(io));
    }

}