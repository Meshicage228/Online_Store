package com.example.orderblservice.exceptions.handler;

import com.example.orderblservice.exceptions.*;
import com.example.orderblservice.util.ErrorMessage;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderExceptionHandler {
    @ExceptionHandler(NoCardFoundException.class)
    public ErrorMessage noCardFound(NoCardFoundException ex){
        String message = ex.getMessage();
        return ErrorMessage.builder()
                .message(message)
                .serviceName("order-service")
                .build();
    }
    @ExceptionHandler(NotEnoughMoneyException.class)
    public ErrorMessage notEnoughMoney(NotEnoughMoneyException ex){
        String message = ex.getMessage();
        return ErrorMessage.builder()
                .message(message)
                .serviceName("order-service")
                .build();
    }
    @ExceptionHandler(OutOfStockException.class)
    public ErrorMessage outOfStock(OutOfStockException ex){
        String message = ex.getMessage();
        return ErrorMessage.builder()
                .message(message)
                .serviceName("order-service")
                .build();
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ErrorMessage noProductFound(ProductNotFoundException ex){
        String message = ex.getMessage();
        return ErrorMessage.builder()
                .message(message)
                .serviceName("order-service")
                .build();
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ErrorMessage noUser(UserNotFoundException ex){
        String message = ex.getMessage();
        return ErrorMessage.builder()
                .message(message)
                .serviceName("order-service")
                .build();
    }
}
