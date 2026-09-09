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
import com.example.demo.service.DepartmentService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/departments")
public class DepartmentMvcController {

    private final DepartmentService departmentService;

    public DepartmentMvcController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public String showDepartments(Model model) {
        if (!model.containsAttribute("department")) {
            model.addAttribute("department", new Department());
        }
        model.addAttribute("departments", departmentService.getAllDepartments());
        return "departments";
    }

    @PostMapping
    public String createDepartment(
            @Valid @ModelAttribute("department") Department department,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("departments", departmentService.getAllDepartments());
            return "departments";
        }

        departmentService.createDepartment(department);
        redirectAttributes.addFlashAttribute("successMessage", "Department added successfully.");
        return "redirect:/departments";
    }
}
