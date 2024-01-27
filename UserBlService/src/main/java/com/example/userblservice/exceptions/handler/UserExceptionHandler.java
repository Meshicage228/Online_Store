package com.example.userblservice.exceptions.handler;

import com.example.userblservice.exceptions.NegativeProductCountException;
import com.example.userblservice.exceptions.ProductNotFoundException;
import com.example.userblservice.exceptions.UserNotFoundException;
import com.example.userblservice.utils.ErrorMessage;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(NegativeProductCountException.class)
    public ErrorMessage negativeProductCount(NegativeProductCountException ex){
        String message = ex.getMessage();
        return ErrorMessage.builder()
                .message(message)
                .serviceName("user-service")
                .build();
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ErrorMessage noProductFound(ProductNotFoundException ex){
        String message = ex.getMessage();
        return ErrorMessage.builder()
                .message(message)
                .serviceName("user-service")
                .build();
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorMessage noUserFound(UserNotFoundException ex){
        String message = ex.getMessage();
        return ErrorMessage.builder()
                .message(message)
                .serviceName("user-service")
                .build();
    }
}
