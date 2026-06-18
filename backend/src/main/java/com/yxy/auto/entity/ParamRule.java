package com.yxy.auto.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("param_rule")
public class ParamRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ruleName;
    private String paramJson;
    private String paramConfig;
    private String createdBy;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    @TableLogic
    private Integer deleted;
}