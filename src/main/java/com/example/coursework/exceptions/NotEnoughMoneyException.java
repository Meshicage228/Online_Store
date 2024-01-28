package com.example.coursework.exceptions;

import lombok.Getter;

@Getter
public class NotEnoughMoneyException extends RuntimeException {
    private final String message;

    public NotEnoughMoneyException(String message) {
        this.message = message;
    }
}
