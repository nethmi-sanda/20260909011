package com.example.demo.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.NominationRequest;
import com.example.demo.model.Nomination;
import com.example.demo.service.NominationService;

@RestController
@RequestMapping("/api/nominations")
public class NominationController {

    private final NominationService nominationService;

    public NominationController(NominationService nominationService) {
        this.nominationService = nominationService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Nomination createNomination(@RequestBody NominationRequest request) {
        return nominationService.createNomination(request);
    }

    @GetMapping
    public List<Nomination> getAllNominations() {
        return nominationService.getAllNominations();
    }

    @GetMapping("/programme/{programmeId}")
    public List<Nomination> getNominationsByTrainingProgramme(
            @PathVariable Long programmeId
    ) {
        return nominationService.getNominationsByTrainingProgramme(programmeId);
    }
}
