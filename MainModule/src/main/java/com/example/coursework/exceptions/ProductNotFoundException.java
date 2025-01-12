package com.example.coursework.exceptions;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends RuntimeException {
    private final String message;

    public ProductNotFoundException(String message) {
        this.message = message;
    }
}
