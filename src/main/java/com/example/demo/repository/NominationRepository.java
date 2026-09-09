package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("""
            select count(nomination)
            from Nomination nomination
            where nomination.officer.id = :officerId
              and lower(nomination.trainingProgramme.title) = lower(:programmeTitle)
              and nomination.trainingProgramme.id <> :currentProgrammeId
              and nomination.trainingProgramme.trainingDate between :startDate and :endDate
              and nomination.status = :status
            """)
    long countPreviousParticipation(
            @Param("officerId") Long officerId,
            @Param("programmeTitle") String programmeTitle,
            @Param("currentProgrammeId") Long currentProgrammeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("status") NominationStatus status
    );
}
