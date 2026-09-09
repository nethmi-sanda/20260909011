package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.dto.EligibilityRuleRequest;
import com.example.demo.model.EligibilityRuleType;
import com.example.demo.service.EligibilityService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/programmes/{programmeId}/eligibility-rules")
public class EligibilityRuleMvcController {

    private final EligibilityService eligibilityService;

    public EligibilityRuleMvcController(EligibilityService eligibilityService) {
        this.eligibilityService = eligibilityService;
    }

    @GetMapping
    public String showRules(@PathVariable Long programmeId, Model model) {
        if (!model.containsAttribute("eligibilityRuleRequest")) {
            model.addAttribute("eligibilityRuleRequest", new EligibilityRuleRequest());
        }
        addPageData(programmeId, model);
        return "eligibility-rules";
    }

    @PostMapping
    public String createRule(
            @PathVariable Long programmeId,
            @Valid @ModelAttribute("eligibilityRuleRequest") EligibilityRuleRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addPageData(programmeId, model);
            return "eligibility-rules";
        }

        try {
            eligibilityService.createRule(programmeId, request);
        } catch (IllegalArgumentException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            addPageData(programmeId, model);
            return "eligibility-rules";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Eligibility rule added successfully.");
        return redirectToRules(programmeId);
    }

    @PostMapping("/{ruleId}/delete")
    public String deleteRule(
            @PathVariable Long programmeId,
            @PathVariable Long ruleId,
            RedirectAttributes redirectAttributes
    ) {
        eligibilityService.deleteRule(programmeId, ruleId);
        redirectAttributes.addFlashAttribute("successMessage", "Eligibility rule removed successfully.");
        return redirectToRules(programmeId);
    }

    private void addPageData(Long programmeId, Model model) {
        model.addAttribute("programme", eligibilityService.requireTrainingProgramme(programmeId));
        model.addAttribute("rules", eligibilityService.getRulesForProgramme(programmeId));
        model.addAttribute("ruleTypes", EligibilityRuleType.values());
    }

    private String redirectToRules(Long programmeId) {
        return "redirect:/programmes/" + programmeId + "/eligibility-rules";
    }
}
