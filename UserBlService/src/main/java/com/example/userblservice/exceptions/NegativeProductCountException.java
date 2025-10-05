package com.example.userblservice.exceptions;

import lombok.Getter;

@Getter
public class NegativeProductCountException extends RuntimeException {
    private final String message;

    public NegativeProductCountException(final String message) {
        this.message = message;
    }
}
