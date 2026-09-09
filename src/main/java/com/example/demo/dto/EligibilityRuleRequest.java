package com.example.demo.dto;

import com.example.demo.model.EligibilityRuleType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class EligibilityRuleRequest {

    @NotNull
    private EligibilityRuleType ruleType;

    @NotBlank
    private String ruleValue;

    public EligibilityRuleRequest() {
    }

    public EligibilityRuleType getRuleType() {
        return ruleType;
    }

    public void setRuleType(EligibilityRuleType ruleType) {
        this.ruleType = ruleType;
    }

    public String getRuleValue() {
        return ruleValue;
    }

    public void setRuleValue(String ruleValue) {
        this.ruleValue = ruleValue;
    }
}
