package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Officer;

public interface OfficerRepository extends JpaRepository<Officer, Long> {

    Optional<Officer> findByEmployeeNumber(String employeeNumber);
}
