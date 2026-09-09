package com.example.demo.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(
        name = "nominations",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_nomination_officer_training_programme",
                columnNames = {"officer_id", "training_programme_id"}
        )
)
public class Nomination {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "officer_id", nullable = false)
    private Officer officer;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "training_programme_id", nullable = false)
    private TrainingProgramme trainingProgramme;

    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "nominated_by_department_id", nullable = false)
    private Department nominatedByDepartment;

    @NotNull
    @Column(nullable = false)
    private LocalDateTime nominationDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "varchar(20) default 'CONFIRMED'")
    private NominationStatus status;

    public Nomination() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Officer getOfficer() {
        return officer;
    }

    public void setOfficer(Officer officer) {
        this.officer = officer;
    }

    public TrainingProgramme getTrainingProgramme() {
        return trainingProgramme;
    }

    public void setTrainingProgramme(TrainingProgramme trainingProgramme) {
        this.trainingProgramme = trainingProgramme;
    }

    public Department getNominatedByDepartment() {
        return nominatedByDepartment;
    }

    public void setNominatedByDepartment(Department nominatedByDepartment) {
        this.nominatedByDepartment = nominatedByDepartment;
    }

    public LocalDateTime getNominationDate() {
        return nominationDate;
    }

    public void setNominationDate(LocalDateTime nominationDate) {
        this.nominationDate = nominationDate;
    }

    public NominationStatus getStatus() {
        return status;
    }

    public void setStatus(NominationStatus status) {
        this.status = status;
    }
}
