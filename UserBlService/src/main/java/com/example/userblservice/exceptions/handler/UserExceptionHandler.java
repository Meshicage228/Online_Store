package com.example.userblservice.exceptions.handler;

import com.example.userblservice.exceptions.ProductNotFoundException;
import com.example.userblservice.exceptions.UserNotFoundException;
import com.example.userblservice.utils.ErrorMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorMessage> noProductFound(ProductNotFoundException ex) {
        String message = ex.getMessage();

        ErrorMessage build = ErrorMessage.builder()
                .message(message)
                .serviceName("product-service")
                .build();

        return ResponseEntity.status(404)
                .body(build);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> noUserFound(UserNotFoundException ex) {
        String message = ex.getMessage();

        ErrorMessage build = ErrorMessage.builder()
                .message(message)
                .serviceName("user-service")
                .build();

        return ResponseEntity.status(404)
                .body(build);
    }
}
