package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.model.Department;
import com.example.demo.model.Officer;
import com.example.demo.service.DepartmentService;
import com.example.demo.service.OfficerService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/officers")
public class OfficerMvcController {

    private final OfficerService officerService;
    private final DepartmentService departmentService;

    public OfficerMvcController(
            OfficerService officerService,
            DepartmentService departmentService
    ) {
        this.officerService = officerService;
        this.departmentService = departmentService;
    }

    @GetMapping
    public String showOfficers(Model model) {
        if (!model.containsAttribute("officer")) {
            Officer officer = new Officer();
            officer.setDepartment(new Department());
            model.addAttribute("officer", officer);
        }
        addPageData(model);
        return "officers";
    }

    @PostMapping
    public String createOfficer(
            @Valid @ModelAttribute("officer") Officer officer,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            addPageData(model);
            return "officers";
        }

        try {
            officerService.createOfficer(officer);
        } catch (IllegalArgumentException exception) {
            bindingResult.rejectValue("employeeNumber", "employeeNumber", exception.getMessage());
            addPageData(model);
            return "officers";
        }

        redirectAttributes.addFlashAttribute("successMessage", "Officer added successfully.");
        return "redirect:/officers";
    }

    private void addPageData(Model model) {
        model.addAttribute("officers", officerService.getAllOfficers());
        model.addAttribute("departments", departmentService.getAllDepartments());
    }
}
