package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.Department;
import com.example.demo.model.Officer;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.OfficerRepository;

@Service
public class OfficerService {

    private final OfficerRepository officerRepository;
    private final DepartmentRepository departmentRepository;

    public OfficerService(
            OfficerRepository officerRepository,
            DepartmentRepository departmentRepository
    ) {
        this.officerRepository = officerRepository;
        this.departmentRepository = departmentRepository;
    }

    public Officer createOfficer(Officer officer) {
        if (officerRepository.findByEmployeeNumber(officer.getEmployeeNumber()).isPresent()) {
            throw new IllegalArgumentException("Employee number already exists");
        }

        Long departmentId = officer.getDepartment().getId();
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
        officer.setDepartment(department);

        return officerRepository.save(officer);
    }

    public List<Officer> getAllOfficers() {
        return officerRepository.findAll();
    }
}
