package com.example.applicationexceptionstarter.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends RuntimeException {
    private final String message;

    public UserNotFoundException(final String message) {
        this.message = message;
    }
}
