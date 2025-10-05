package com.example.applicationexceptionstarter.handler;

import com.example.applicationexceptionstarter.dto.ErrorMessage;
import com.example.applicationexceptionstarter.exception.NotEnoughMoneyException;
import com.example.applicationexceptionstarter.exception.OutOfStockException;
import com.example.applicationexceptionstarter.exception.ProductNotFoundException;
import com.example.applicationexceptionstarter.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectExceptionHandler {

    @Value("${spring.application.name:unknown-service}")
    private String serviceName;

    private ErrorMessage buildErrorMessage(final Exception ex) {
        return ErrorMessage.builder()
                .serviceName(serviceName)
                .message(ex.getMessage())
                .build();
    }

    private ResponseEntity<ErrorMessage> buildErrorResponse(Exception ex, int statusCode) {
        return ResponseEntity.status(statusCode)
                .body(buildErrorMessage(ex));
    }

    @ExceptionHandler(NotEnoughMoneyException.class)
    public ResponseEntity<ErrorMessage> handleNotEnoughMoney(final NotEnoughMoneyException ex) {
        return buildErrorResponse(ex, 400);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleProductNotFound(final ProductNotFoundException ex) {
        return buildErrorResponse(ex, 404);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorMessage> handleUserNotFound(final UserNotFoundException ex) {
        return buildErrorResponse(ex, 404);
    }

    @ExceptionHandler(OutOfStockException.class)
    public ResponseEntity<ErrorMessage> handleOutOfStock(final OutOfStockException ex) {
        return buildErrorResponse(ex, 400);
    }
}
