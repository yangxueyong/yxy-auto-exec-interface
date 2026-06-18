package com.yxy.auto.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("api_test_record")
public class ApiTestRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long apiDefinitionId;
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
    private String createdBy;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    @TableLogic
    private Integer deleted;
}