package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.EligibilityRule;
import com.example.demo.model.EligibilityRuleType;

public interface EligibilityRuleRepository extends JpaRepository<EligibilityRule, Long> {

    List<EligibilityRule> findByTrainingProgrammeIdOrderByRuleTypeAscIdAsc(
            Long trainingProgrammeId
    );

    boolean existsByTrainingProgrammeIdAndRuleTypeAndRuleValueIgnoreCase(
            Long trainingProgrammeId,
            EligibilityRuleType ruleType,
            String ruleValue
    );
}
