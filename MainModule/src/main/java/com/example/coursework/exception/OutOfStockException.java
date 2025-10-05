package com.example.coursework.exception;

import lombok.Getter;

@Getter
public class OutOfStockException extends RuntimeException {
    private final String message;

    public OutOfStockException(final String message) {
        this.message = message;
    }
}
