package com.example.coursework.exceptions;

import lombok.Getter;

@Getter
public class OutOfStockException extends RuntimeException {
    private final String message;

    public OutOfStockException(String message) {
        this.message = message;
    }
}
