package com.yxy.auto.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("api_definition")
public class ApiDefinition {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String apiName;
    private String apiUrl;
    private String apiMethod;
    private Long paramRuleId;
    private String responseRuleIds;
    private String createdBy;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    @TableLogic
    private Integer deleted;
}