package com.example.demo.dto;

import jakarta.validation.constraints.NotNull;

public class NominationRequest {

    @NotNull(message = "Please select an officer")
    private Long officerId;

    @NotNull(message = "Please select a training programme")
    private Long trainingProgrammeId;

    @NotNull(message = "Please select a nominating department")
    private Long departmentId;

    public NominationRequest() {
    }

    public Long getOfficerId() {
        return officerId;
    }

    public void setOfficerId(Long officerId) {
        this.officerId = officerId;
    }

    public Long getTrainingProgrammeId() {
        return trainingProgrammeId;
    }

    public void setTrainingProgrammeId(Long trainingProgrammeId) {
        this.trainingProgrammeId = trainingProgrammeId;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }
}
