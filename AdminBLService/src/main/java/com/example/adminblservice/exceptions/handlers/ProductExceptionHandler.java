package com.example.adminblservice.exceptions.handlers;

import com.example.adminblservice.exceptions.ProductNotFoundException;
import com.example.adminblservice.util.ErrorMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Base64;

@RestControllerAdvice
public class ProductExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorMessage> noProductFound(ProductNotFoundException ex) {
        String message = ex.getMessage();
        ErrorMessage build = ErrorMessage.builder()
                .message(message)
                .serviceName("product-service")
                .build();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(build);
    }
}
