package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.TrainingProgramme;
import com.example.demo.repository.TrainingProgrammeRepository;

@Service
public class TrainingProgrammeService {

    private final TrainingProgrammeRepository trainingProgrammeRepository;

    public TrainingProgrammeService(TrainingProgrammeRepository trainingProgrammeRepository) {
        this.trainingProgrammeRepository = trainingProgrammeRepository;
    }

    public TrainingProgramme createTrainingProgramme(TrainingProgramme trainingProgramme) {
        return trainingProgrammeRepository.save(trainingProgramme);
    }

    public List<TrainingProgramme> getAllTrainingProgrammes() {
        return trainingProgrammeRepository.findAll();
    }
}
