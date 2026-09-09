package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.dto.NominationRequest;
import com.example.demo.exception.DuplicateNominationException;
import com.example.demo.model.Department;
import com.example.demo.model.Nomination;
import com.example.demo.model.Officer;
import com.example.demo.model.TrainingProgramme;
import com.example.demo.repository.DepartmentRepository;
import com.example.demo.repository.NominationRepository;
import com.example.demo.repository.OfficerRepository;
import com.example.demo.repository.TrainingProgrammeRepository;

@Service
public class NominationService {

    private final NominationRepository nominationRepository;
    private final OfficerRepository officerRepository;
    private final TrainingProgrammeRepository trainingProgrammeRepository;
    private final DepartmentRepository departmentRepository;

    public NominationService(
            NominationRepository nominationRepository,
            OfficerRepository officerRepository,
            TrainingProgrammeRepository trainingProgrammeRepository,
            DepartmentRepository departmentRepository
    ) {
        this.nominationRepository = nominationRepository;
        this.officerRepository = officerRepository;
        this.trainingProgrammeRepository = trainingProgrammeRepository;
        this.departmentRepository = departmentRepository;
    }

    public Nomination createNomination(NominationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Nomination request must not be null");
        }

        Officer officer = officerRepository.findById(request.getOfficerId())
                .orElseThrow(() -> new RuntimeException("Officer not found"));

        TrainingProgramme trainingProgramme = trainingProgrammeRepository
                .findById(request.getTrainingProgrammeId())
                .orElseThrow(() -> new RuntimeException("Training programme not found"));

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new RuntimeException("Department not found"));

        boolean duplicateExists = nominationRepository
                .existsByTrainingProgrammeIdAndOfficerId(
                        request.getTrainingProgrammeId(),
                        request.getOfficerId()
                );

        if (duplicateExists) {
            throw new DuplicateNominationException(
                    "This officer has already been nominated for this training programme."
            );
        }

        Nomination nomination = new Nomination();
        nomination.setOfficer(officer);
        nomination.setTrainingProgramme(trainingProgramme);
        nomination.setNominatedByDepartment(department);
        nomination.setNominationDate(LocalDateTime.now());

        return nominationRepository.save(nomination);
    }

    public List<Nomination> getAllNominations() {
        return nominationRepository.findAll();
    }

    public List<Nomination> getNominationsByTrainingProgramme(Long trainingProgrammeId) {
        return nominationRepository.findByTrainingProgrammeId(trainingProgrammeId);
    }
}
