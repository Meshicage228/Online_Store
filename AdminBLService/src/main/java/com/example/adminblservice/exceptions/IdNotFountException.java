package com.example.adminblservice.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public class IdNotFountException extends RuntimeException{
    private String message;

    public IdNotFountException(String message) {
        this.message = message;
    }
}
