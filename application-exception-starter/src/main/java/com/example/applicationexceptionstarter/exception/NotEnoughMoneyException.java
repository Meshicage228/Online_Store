package com.example.applicationexceptionstarter.exception;

import lombok.Getter;

@Getter
public class NotEnoughMoneyException extends RuntimeException {
    private final String message;

    public NotEnoughMoneyException(final String message) {
        this.message = message;
    }
}
