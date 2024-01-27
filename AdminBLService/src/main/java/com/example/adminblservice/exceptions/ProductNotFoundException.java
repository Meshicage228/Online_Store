package com.example.adminblservice.exceptions;

import lombok.Getter;

@Getter
public class ProductNotFoundException extends RuntimeException {
    private String message;

    public ProductNotFoundException(String message) {
        this.message = message;
    }
}
