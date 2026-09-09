package com.example.demo.config;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.example.demo.model.Department;
import com.example.demo.model.Officer;
import com.example.demo.model.TrainingProgramme;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.OfficerRepository;
import com.example.demo.repository.TrainingProgrammeRepository;

@Component
@Profile("dev")
public class DevelopmentDataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DevelopmentDataInitializer.class);

    private final DepartmentRepository departmentRepository;
    private final OfficerRepository officerRepository;
    private final TrainingProgrammeRepository trainingProgrammeRepository;

    public DevelopmentDataInitializer(
            DepartmentRepository departmentRepository,
            OfficerRepository officerRepository,
            TrainingProgrammeRepository trainingProgrammeRepository
    ) {
        this.departmentRepository = departmentRepository;
        this.officerRepository = officerRepository;
        this.trainingProgrammeRepository = trainingProgrammeRepository;
    }

    @Override
    public void run(String... args) {
        List<Department> departments = createDepartmentsIfEmpty();
        createOfficersIfEmpty(departments);
        createTrainingProgrammesIfEmpty();
        logDevelopmentData();
    }

    private List<Department> createDepartmentsIfEmpty() {
        if (departmentRepository.count() == 0) {
            Department finance = new Department();
            finance.setName("Finance Division");

            Department administration = new Department();
            administration.setName("Administration Division");

            return departmentRepository.saveAll(List.of(finance, administration));
        }

        return departmentRepository.findAll();
    }

    private void createOfficersIfEmpty(List<Department> departments) {
        if (officerRepository.count() != 0) {
            return;
        }

        Department finance = findDepartment(departments, "Finance Division");
        Department administration = findDepartment(departments, "Administration Division");

        Officer firstOfficer = new Officer();
        firstOfficer.setEmployeeNumber("EMP001");
        firstOfficer.setFullName("A. Perera");
        firstOfficer.setEmail("aperera@example.com");
        firstOfficer.setDepartment(finance);

        Officer secondOfficer = new Officer();
        secondOfficer.setEmployeeNumber("EMP002");
        secondOfficer.setFullName("N. Silva");
        secondOfficer.setEmail("nsilva@example.com");
        secondOfficer.setDepartment(administration);

        officerRepository.saveAll(List.of(firstOfficer, secondOfficer));
    }

    private void createTrainingProgrammesIfEmpty() {
        if (trainingProgrammeRepository.count() != 0) {
            return;
        }

        TrainingProgramme javaTraining = new TrainingProgramme();
        javaTraining.setTitle("Java Programming Training");
        javaTraining.setTrainingDate(LocalDate.now().plusMonths(1));
        javaTraining.setVenue("Main Training Hall");
        javaTraining.setTrainerName("Mr. Fernando");
        javaTraining.setMaxParticipants(30);

        TrainingProgramme cyberSecurity = new TrainingProgramme();
        cyberSecurity.setTitle("Cyber Security Awareness");
        cyberSecurity.setTrainingDate(LocalDate.now().plusMonths(2));
        cyberSecurity.setVenue("Conference Room");
        cyberSecurity.setTrainerName("Ms. Perera");
        cyberSecurity.setMaxParticipants(40);

        trainingProgrammeRepository.saveAll(List.of(javaTraining, cyberSecurity));
    }

    private Department findDepartment(List<Department> departments, String name) {
        return departments.stream()
                .filter(department -> name.equals(department.getName()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Required development department not found: " + name
                ));
    }

    private void logDevelopmentData() {
        logger.info(
                "Development data counts: departments={}, officers={}, training programmes={}",
                departmentRepository.count(),
                officerRepository.count(),
                trainingProgrammeRepository.count()
        );

        departmentRepository.findAll().forEach(department -> logger.info(
                "Department ID: {}, name: {}",
                department.getId(),
                department.getName()
        ));

        officerRepository.findAll().forEach(officer -> logger.info(
                "Officer ID: {}, employee number: {}",
                officer.getId(),
                officer.getEmployeeNumber()
        ));

        trainingProgrammeRepository.findAll().forEach(programme -> logger.info(
                "Training programme ID: {}, title: {}",
                programme.getId(),
                programme.getTitle()
        ));
    }
}
