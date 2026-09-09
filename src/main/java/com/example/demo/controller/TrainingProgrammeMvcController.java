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

import jakarta.validation.Valid;

@Controller
@RequestMapping("/programmes")
public class TrainingProgrammeMvcController {

    private final TrainingProgrammeService trainingProgrammeService;

    public TrainingProgrammeMvcController(TrainingProgrammeService trainingProgrammeService) {
        this.trainingProgrammeService = trainingProgrammeService;
    }

    @GetMapping
    public String showTrainingProgrammes(Model model) {
        if (!model.containsAttribute("trainingProgramme")) {
            model.addAttribute("trainingProgramme", new TrainingProgramme());
        }
        model.addAttribute("programmes", trainingProgrammeService.getAllTrainingProgrammes());
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
            model.addAttribute("programmes", trainingProgrammeService.getAllTrainingProgrammes());
            return "training-programmes";
        }

        trainingProgrammeService.createTrainingProgramme(trainingProgramme);
        redirectAttributes.addFlashAttribute("successMessage", "Training programme added successfully.");
        return "redirect:/programmes";
    }
}
