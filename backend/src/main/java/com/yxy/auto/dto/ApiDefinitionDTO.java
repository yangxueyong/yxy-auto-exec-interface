package com.yxy.auto.dto;

import lombok.Data;

@Data
public class ApiDefinitionDTO {
    private Long id;
    private String apiName;
    private String apiUrl;
    private String apiMethod;
    private Long paramRuleId;
    private String responseRuleIds;
    private String paramRuleName;
    private String responseRuleNames;
}