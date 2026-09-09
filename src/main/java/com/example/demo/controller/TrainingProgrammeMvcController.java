package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.TrainingProgramme;
import com.example.demo.service.TrainingProgrammeService;
import com.example.demo.service.NominationService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/programmes")
public class TrainingProgrammeMvcController {

    private final TrainingProgrammeService trainingProgrammeService;
    private final NominationService nominationService;

    public TrainingProgrammeMvcController(
            TrainingProgrammeService trainingProgrammeService,
            NominationService nominationService
    ) {
        this.trainingProgrammeService = trainingProgrammeService;
        this.nominationService = nominationService;
    }

    @GetMapping
    public String showTrainingProgrammes(Model model) {
        if (!model.containsAttribute("trainingProgramme")) {
            model.addAttribute("trainingProgramme", new TrainingProgramme());
        }
        addProgrammeData(model);
        return "training-programmes";
    }

    @PostMapping
    public String createTrainingProgramme(
            @Valid @ModelAttribute("trainingProgramme") TrainingProgramme trainingProgramme,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addProgrammeData(model);
            return "training-programmes";
        }

        trainingProgrammeService.createTrainingProgramme(trainingProgramme);
        redirectAttributes.addFlashAttribute("successMessage", "Training programme added successfully.");
        return "redirect:/programmes";
    }

    private void addProgrammeData(Model model) {
        model.addAttribute("programmes", trainingProgrammeService.getAllTrainingProgrammes());
        model.addAttribute("capacitySummaries", nominationService.getProgrammeCapacitySummaries());
    }
}
