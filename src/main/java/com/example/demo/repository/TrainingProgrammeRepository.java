package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.model.TrainingProgramme;

import jakarta.persistence.LockModeType;

public interface TrainingProgrammeRepository extends JpaRepository<TrainingProgramme, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select programme from TrainingProgramme programme where programme.id = :id")
    Optional<TrainingProgramme> findByIdForUpdate(@Param("id") Long id);
}
