package com.yxy.auto.vo;

import lombok.Data;

@Data
public class ApiTestResultVO {
    private Integer totalCount;
    private Integer successCount;
    private Integer failCount;
    private Long maxTime;
    private Long minTime;
    private Double avgTime;
    private String status;
}