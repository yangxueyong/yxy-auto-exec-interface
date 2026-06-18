package com.yxy.auto.dto;

import lombok.Data;

@Data
public class UserInfoDTO {
    private String userId;
    private String userName;
    private Integer gender;
    private String address;
    private String phone;
    private String status;
    private String remark;
}