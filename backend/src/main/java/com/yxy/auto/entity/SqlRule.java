package com.yxy.auto.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("sql_rule")
public class SqlRule {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String ruleName;
    private String ruleSql;
    private String ruleFields;
    private String createdBy;
    private LocalDateTime createdTime;
    private LocalDateTime updatedTime;
    @TableLogic
    private Integer deleted;
}