package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Nomination;
import com.example.demo.model.NominationStatus;

public interface NominationRepository extends JpaRepository<Nomination, Long> {

    boolean existsByTrainingProgrammeIdAndOfficerId(
            Long trainingProgrammeId,
            Long officerId
    );

    List<Nomination> findByTrainingProgrammeId(Long trainingProgrammeId);

    long countByTrainingProgrammeIdAndStatus(
            Long trainingProgrammeId,
            NominationStatus status
    );

    List<Nomination> findByTrainingProgrammeIdAndStatusOrderByNominationDateAsc(
            Long trainingProgrammeId,
            NominationStatus status
    );
}
