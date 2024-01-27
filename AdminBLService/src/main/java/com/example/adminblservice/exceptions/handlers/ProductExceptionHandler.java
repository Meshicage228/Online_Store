package com.example.adminblservice.exceptions.handlers;

import com.example.adminblservice.exceptions.ProductNotFoundException;
import com.example.adminblservice.util.ErrorMessage;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProductExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ErrorMessage noProductFound(ProductNotFoundException ex){
        String message = ex.getMessage();
        return ErrorMessage.builder()
                .message(message)
                .serviceName("product-service")
                .build();
    }
}
