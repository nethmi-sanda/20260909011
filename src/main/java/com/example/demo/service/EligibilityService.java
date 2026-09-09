package com.example.demo.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.EligibilityRuleRequest;
import com.example.demo.exception.IneligibleOfficerException;
import com.example.demo.model.EligibilityRule;
import com.example.demo.model.EligibilityRuleType;
import com.example.demo.model.NominationStatus;
import com.example.demo.model.Officer;
import com.example.demo.model.TrainingProgramme;
import com.example.demo.repository.EligibilityRuleRepository;
import com.example.demo.repository.NominationRepository;
import com.example.demo.repository.TrainingProgrammeRepository;

@Service
public class EligibilityService {

    private final EligibilityRuleRepository eligibilityRuleRepository;
    private final NominationRepository nominationRepository;
    private final TrainingProgrammeRepository trainingProgrammeRepository;

    public EligibilityService(
            EligibilityRuleRepository eligibilityRuleRepository,
            NominationRepository nominationRepository,
            TrainingProgrammeRepository trainingProgrammeRepository
    ) {
        this.eligibilityRuleRepository = eligibilityRuleRepository;
        this.nominationRepository = nominationRepository;
        this.trainingProgrammeRepository = trainingProgrammeRepository;
    }

    public void validateEligibility(Officer officer, TrainingProgramme trainingProgramme) {
        List<EligibilityRule> rules = eligibilityRuleRepository
                .findByTrainingProgrammeIdOrderByRuleTypeAscIdAsc(trainingProgramme.getId());

        validateDepartmentRules(officer, rules);
        validateDesignationRules(officer, rules);
        validateMinimumServiceRules(officer, rules);
        validatePreviousAttendanceRules(officer, trainingProgramme, rules);
    }

    public List<EligibilityRule> getRulesForProgramme(Long trainingProgrammeId) {
        requireTrainingProgramme(trainingProgrammeId);
        return eligibilityRuleRepository
                .findByTrainingProgrammeIdOrderByRuleTypeAscIdAsc(trainingProgrammeId);
    }

    @Transactional
    public EligibilityRule createRule(
            Long trainingProgrammeId,
            EligibilityRuleRequest request
    ) {
        TrainingProgramme trainingProgramme = requireTrainingProgramme(trainingProgrammeId);
        if (request == null || request.getRuleType() == null) {
            throw new IllegalArgumentException("Eligibility rule type is required.");
        }

        String normalizedValue = normalizeRuleValue(request.getRuleType(), request.getRuleValue());
        if (eligibilityRuleRepository
                .existsByTrainingProgrammeIdAndRuleTypeAndRuleValueIgnoreCase(
                        trainingProgrammeId,
                        request.getRuleType(),
                        normalizedValue
                )) {
            throw new IllegalArgumentException("This eligibility rule already exists.");
        }

        EligibilityRule rule = new EligibilityRule();
        rule.setTrainingProgramme(trainingProgramme);
        rule.setRuleType(request.getRuleType());
        rule.setRuleValue(normalizedValue);
        return eligibilityRuleRepository.save(rule);
    }

    @Transactional
    public void deleteRule(Long trainingProgrammeId, Long ruleId) {
        EligibilityRule rule = eligibilityRuleRepository.findById(ruleId)
                .orElseThrow(() -> new IllegalArgumentException("Eligibility rule not found."));

        if (!rule.getTrainingProgramme().getId().equals(trainingProgrammeId)) {
            throw new IllegalArgumentException("Eligibility rule does not belong to this programme.");
        }

        eligibilityRuleRepository.delete(rule);
    }

    public TrainingProgramme requireTrainingProgramme(Long trainingProgrammeId) {
        return trainingProgrammeRepository.findById(trainingProgrammeId)
                .orElseThrow(() -> new IllegalArgumentException("Training programme not found."));
    }

    private void validateDepartmentRules(Officer officer, List<EligibilityRule> rules) {
        List<EligibilityRule> departmentRules = rulesOfType(rules, EligibilityRuleType.DEPARTMENT);
        if (departmentRules.isEmpty()) {
            return;
        }

        String departmentName = officer.getDepartment() == null
                ? null
                : officer.getDepartment().getName();
        boolean eligible = departmentName != null && departmentRules.stream()
                .anyMatch(rule -> departmentName.equalsIgnoreCase(rule.getRuleValue()));

        if (!eligible) {
            throw new IneligibleOfficerException(
                    "Officer's department is not eligible for this training programme."
            );
        }
    }

    private void validateDesignationRules(Officer officer, List<EligibilityRule> rules) {
        List<EligibilityRule> designationRules = rulesOfType(rules, EligibilityRuleType.DESIGNATION);
        if (designationRules.isEmpty()) {
            return;
        }

        String designation = officer.getDesignation();
        boolean eligible = designation != null && designationRules.stream()
                .anyMatch(rule -> designation.equalsIgnoreCase(rule.getRuleValue()));

        if (!eligible) {
            throw new IneligibleOfficerException(
                    "Officer does not meet the required designation."
            );
        }
    }

    private void validateMinimumServiceRules(Officer officer, List<EligibilityRule> rules) {
        List<EligibilityRule> serviceRules = rulesOfType(
                rules,
                EligibilityRuleType.MIN_YEARS_SERVICE
        );
        if (serviceRules.isEmpty()) {
            return;
        }

        if (officer.getServiceStartDate() == null
                || officer.getServiceStartDate().isAfter(LocalDate.now())) {
            throw new IneligibleOfficerException(
                    "Officer does not meet the minimum years of service requirement."
            );
        }

        long yearsOfService = ChronoUnit.YEARS.between(
                officer.getServiceStartDate(),
                LocalDate.now()
        );
        int requiredYears = serviceRules.stream()
                .mapToInt(rule -> parsePositiveNumber(rule.getRuleValue(), "years of service"))
                .max()
                .orElse(0);

        if (yearsOfService < requiredYears) {
            throw new IneligibleOfficerException(
                    "Officer does not meet the minimum years of service requirement."
            );
        }
    }

    private void validatePreviousAttendanceRules(
            Officer officer,
            TrainingProgramme trainingProgramme,
            List<EligibilityRule> rules
    ) {
        List<EligibilityRule> attendanceRules = rulesOfType(
                rules,
                EligibilityRuleType.NOT_ATTENDED_WITHIN_MONTHS
        );
        if (attendanceRules.isEmpty()) {
            return;
        }

        int restrictedMonths = attendanceRules.stream()
                .mapToInt(rule -> parsePositiveNumber(rule.getRuleValue(), "restricted months"))
                .max()
                .orElse(0);
        LocalDate today = LocalDate.now();
        long previousParticipationCount = nominationRepository.countPreviousParticipation(
                officer.getId(),
                trainingProgramme.getTitle(),
                trainingProgramme.getId(),
                today.minusMonths(restrictedMonths),
                today,
                NominationStatus.CONFIRMED
        );

        if (previousParticipationCount > 0) {
            throw new IneligibleOfficerException(
                    "Officer attended this training programme within the restricted period."
            );
        }
    }

    private List<EligibilityRule> rulesOfType(
            List<EligibilityRule> rules,
            EligibilityRuleType ruleType
    ) {
        return rules.stream()
                .filter(rule -> rule.getRuleType() == ruleType)
                .toList();
    }

    private String normalizeRuleValue(EligibilityRuleType ruleType, String ruleValue) {
        if (ruleValue == null || ruleValue.isBlank()) {
            throw new IllegalArgumentException("Eligibility rule value is required.");
        }

        String trimmedValue = ruleValue.trim();
        if (ruleType == EligibilityRuleType.MIN_YEARS_SERVICE
                || ruleType == EligibilityRuleType.NOT_ATTENDED_WITHIN_MONTHS) {
            return String.valueOf(parsePositiveNumber(trimmedValue, "rule value"));
        }
        return trimmedValue;
    }

    private int parsePositiveNumber(String value, String fieldName) {
        try {
            int number = Integer.parseInt(value);
            if (number <= 0) {
                throw new IllegalArgumentException();
            }
            return number;
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "The " + fieldName + " value must be a positive whole number."
            );
        }
    }
}
