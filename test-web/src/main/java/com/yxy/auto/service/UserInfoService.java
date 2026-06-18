package com.yxy.auto.service;

import com.yxy.auto.dto.UserInfoDTO;
import com.yxy.auto.io.QueryUserInfoIO;

import java.util.List;

public interface UserInfoService {
    List<UserInfoDTO> getByIo(QueryUserInfoIO io);
}