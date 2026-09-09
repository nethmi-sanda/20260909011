package com.example.demo.exception;

public class DuplicateNominationException extends RuntimeException {

    public DuplicateNominationException(String message) {
        super(message);
    }
}
