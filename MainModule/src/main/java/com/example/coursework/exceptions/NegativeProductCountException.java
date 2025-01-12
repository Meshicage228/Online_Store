package com.example.coursework.exceptions;

import lombok.Getter;

@Getter
public class NegativeProductCountException extends RuntimeException {
    private final String message;

    public NegativeProductCountException(String message) {
        this.message = message;
    }
}
