package com.example.coursework.exceptions.handlers;

import com.example.coursework.exceptions.IdNotFountException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class StoreExceptionHandler {
    @ExceptionHandler(IdNotFountException.class)
    public String getExc(IdNotFountException ex){
        String message = ex.getMessage();

        System.out.println(message);
        return message;
    }
}
