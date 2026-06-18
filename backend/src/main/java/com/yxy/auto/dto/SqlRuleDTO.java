package com.yxy.auto.dto;

import lombok.Data;
import java.util.List;

@Data
public class SqlRuleDTO {
    private Long id;
    private String ruleName;
    private String ruleSql;
    private String ruleFields;
    private List<String> fieldList;
}