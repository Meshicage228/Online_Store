package com.example.orderblservice.exceptions;

import lombok.Getter;

@Getter
public class NoCardFoundException extends RuntimeException {
    private final String message;

    public NoCardFoundException(String message) {
        this.message = message;
    }
}
