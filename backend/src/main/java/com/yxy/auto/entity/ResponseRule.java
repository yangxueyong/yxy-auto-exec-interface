package com.yxy.auto.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("response_rule")
public class ResponseRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ruleName;
    private String spelExpression;
    private String createdBy;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    @TableLogic
    private Integer deleted;
}