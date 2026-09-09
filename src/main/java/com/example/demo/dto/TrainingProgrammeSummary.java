package com.example.demo.dto;

import com.example.demo.model.TrainingProgramme;

public class TrainingProgrammeSummary {

    private final TrainingProgramme trainingProgramme;
    private final long confirmedParticipants;
    private final long waitingListCount;
    private final long availableSeats;

    public TrainingProgrammeSummary(
            TrainingProgramme trainingProgramme,
            long confirmedParticipants,
            long waitingListCount,
            long availableSeats
    ) {
        this.trainingProgramme = trainingProgramme;
        this.confirmedParticipants = confirmedParticipants;
        this.waitingListCount = waitingListCount;
        this.availableSeats = availableSeats;
    }

    public TrainingProgramme getTrainingProgramme() {
        return trainingProgramme;
    }

    public long getConfirmedParticipants() {
        return confirmedParticipants;
    }

    public long getWaitingListCount() {
        return waitingListCount;
    }

    public long getAvailableSeats() {
        return availableSeats;
    }
}
