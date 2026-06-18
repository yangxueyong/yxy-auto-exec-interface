package com.yxy.auto.dto;

import lombok.Data;

@Data
public class ResponseRuleDTO {
    private Long id;
    private String ruleName;
    private String spelExpression;
}