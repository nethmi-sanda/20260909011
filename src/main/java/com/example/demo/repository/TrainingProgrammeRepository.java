package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.TrainingProgramme;

public interface TrainingProgrammeRepository extends JpaRepository<TrainingProgramme, Long> {
}
