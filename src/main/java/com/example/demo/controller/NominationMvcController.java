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

import com.example.demo.dto.NominationRequest;
import com.example.demo.exception.DuplicateNominationException;
import com.example.demo.model.Nomination;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.NominationService;
import com.example.demo.service.OfficerService;
import com.example.demo.service.TrainingProgrammeService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/nominations")
public class NominationMvcController {

    private final NominationService nominationService;
    private final OfficerService officerService;
    private final TrainingProgrammeService trainingProgrammeService;
    private final DepartmentService departmentService;

    public NominationMvcController(
            NominationService nominationService,
            OfficerService officerService,
            TrainingProgrammeService trainingProgrammeService,
            DepartmentService departmentService
    ) {
        this.nominationService = nominationService;
        this.officerService = officerService;
        this.trainingProgrammeService = trainingProgrammeService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String showNominations(Model model) {
        model.addAttribute("nominations", nominationService.getAllNominations());
        return "nominations";
    }

    @GetMapping("/new")
    public String showNominationForm(Model model) {
        if (!model.containsAttribute("nominationRequest")) {
            model.addAttribute("nominationRequest", new NominationRequest());
        }
        addFormData(model);
        return "nomination-form";
    }

    @PostMapping("/new")
    public String createNomination(
            @Valid @ModelAttribute("nominationRequest") NominationRequest request,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addFormData(model);
            return "nomination-form";
        }

        try {
            Nomination nomination = nominationService.createNomination(request);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "Nomination created with status: " + nomination.getStatus() + "."
            );
        } catch (DuplicateNominationException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            addFormData(model);
            return "nomination-form";
        } catch (RuntimeException exception) {
            model.addAttribute("errorMessage", exception.getMessage());
            addFormData(model);
            return "nomination-form";
        }

        return "redirect:/nominations";
    }

    @PostMapping("/{id}/cancel")
    public String cancelNomination(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        nominationService.cancelNomination(id);
        redirectAttributes.addFlashAttribute("successMessage", "Nomination cancelled successfully.");
        return "redirect:/nominations";
    }

    private void addFormData(Model model) {
        model.addAttribute("officers", officerService.getAllOfficers());
        model.addAttribute("programmes", trainingProgrammeService.getAllTrainingProgrammes());
        model.addAttribute("departments", departmentService.getAllDepartments());
    }
}
