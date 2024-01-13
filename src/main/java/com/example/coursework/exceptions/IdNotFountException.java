package com.example.coursework.exceptions;

public class IdNotFountException extends RuntimeException{
    private String message;

    public IdNotFountException(String message) {
        this.message = message;
    }
}
