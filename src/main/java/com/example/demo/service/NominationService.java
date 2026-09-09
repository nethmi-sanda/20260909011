package com.example.demo.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.dto.NominationRequest;
import com.example.demo.dto.TrainingProgrammeSummary;
import com.example.demo.exception.DuplicateNominationException;
import com.example.demo.model.Department;
import com.example.demo.model.Nomination;
import com.example.demo.model.Officer;
import com.example.demo.model.NominationStatus;
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

    @Transactional
    public Nomination createNomination(NominationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Nomination request must not be null");
        }

        Officer officer = officerRepository.findById(request.getOfficerId())
                .orElseThrow(() -> new RuntimeException("Officer not found"));

        TrainingProgramme trainingProgramme = trainingProgrammeRepository
                .findByIdForUpdate(request.getTrainingProgrammeId())
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

        long confirmedCount = nominationRepository.countByTrainingProgrammeIdAndStatus(
                trainingProgramme.getId(),
                NominationStatus.CONFIRMED
        );
        if (confirmedCount < trainingProgramme.getMaxParticipants()) {
            nomination.setStatus(NominationStatus.CONFIRMED);
        } else {
            nomination.setStatus(NominationStatus.WAITING);
        }

        nomination.setNominationDate(LocalDateTime.now());

        return nominationRepository.save(nomination);
    }

    public List<Nomination> getAllNominations() {
        return nominationRepository.findAll();
    }

    public List<Nomination> getNominationsByTrainingProgramme(Long trainingProgrammeId) {
        return nominationRepository.findByTrainingProgrammeId(trainingProgrammeId);
    }

    @Transactional
    public Nomination cancelNomination(Long nominationId) {
        Nomination nomination = nominationRepository.findById(nominationId)
                .orElseThrow(() -> new RuntimeException("Nomination not found"));

        Long trainingProgrammeId = nomination.getTrainingProgramme().getId();
        trainingProgrammeRepository.findByIdForUpdate(trainingProgrammeId)
                .orElseThrow(() -> new RuntimeException("Training programme not found"));

        if (nomination.getStatus() == NominationStatus.CANCELLED) {
            return nomination;
        }

        boolean confirmedSeatReleased = nomination.getStatus() == NominationStatus.CONFIRMED;
        nomination.setStatus(NominationStatus.CANCELLED);
        Nomination cancelledNomination = nominationRepository.save(nomination);

        if (confirmedSeatReleased) {
            List<Nomination> waitingNominations = nominationRepository
                    .findByTrainingProgrammeIdAndStatusOrderByNominationDateAsc(
                            trainingProgrammeId,
                            NominationStatus.WAITING
                    );

            if (!waitingNominations.isEmpty()) {
                Nomination nextWaitingNomination = waitingNominations.get(0);
                nextWaitingNomination.setStatus(NominationStatus.CONFIRMED);
                nominationRepository.save(nextWaitingNomination);
            }
        }

        return cancelledNomination;
    }

    public List<TrainingProgrammeSummary> getProgrammeCapacitySummaries() {
        return trainingProgrammeRepository.findAll().stream()
                .map(this::createCapacitySummary)
                .toList();
    }

    private TrainingProgrammeSummary createCapacitySummary(TrainingProgramme programme) {
        long confirmedCount = nominationRepository.countByTrainingProgrammeIdAndStatus(
                programme.getId(),
                NominationStatus.CONFIRMED
        );
        long waitingCount = nominationRepository.countByTrainingProgrammeIdAndStatus(
                programme.getId(),
                NominationStatus.WAITING
        );
        long availableSeats = Math.max(0, programme.getMaxParticipants() - confirmedCount);

        return new TrainingProgrammeSummary(
                programme,
                confirmedCount,
                waitingCount,
                availableSeats
        );
    }
}
