package com.yxy.auto.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class ParamRuleDTO {
    private Long id;
    private String ruleName;
    private String paramJson;
    private List<ParamConfig> paramConfigs;
    
    @Data
    public static class ParamConfig {
        private String paramName;
        private String paramType; // fixed, db, random_time, random_date, manual
        private String fixedValue;
        private Long sqlRuleId;
        private String sqlField;
        private List<String> manualValues;
    }
}