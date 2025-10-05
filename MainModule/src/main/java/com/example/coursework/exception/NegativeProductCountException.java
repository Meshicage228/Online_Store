package com.example.coursework.exception;

import lombok.Getter;

@Getter
public class NegativeProductCountException extends RuntimeException {
    private final String message;

    public NegativeProductCountException(final String message) {
        this.message = message;
    }
}
