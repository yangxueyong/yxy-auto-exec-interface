package com.yxy.auto.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ApiTestRecordDTO {
    private Long id;
    private Long apiDefinitionId;
    private String apiName;
    private String testName;
    private Integer totalCount;
    private Integer successCount;
    private Integer failCount;
    private Long maxTime;
    private Long minTime;
    private BigDecimal avgTime;
    private Integer threadCount;
    private Integer testDuration;
    private Integer status;
    private String resultDetail;
}