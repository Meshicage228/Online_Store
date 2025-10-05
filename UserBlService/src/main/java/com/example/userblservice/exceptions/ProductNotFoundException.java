package com.example.userblservice.exceptions;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends RuntimeException {
    private final String message;

    public ProductNotFoundException(final String message) {
        this.message = message;
    }
}
