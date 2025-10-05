package com.example.coursework.exception.handler;

import com.example.coursework.exception.NegativeProductCountException;
import com.example.coursework.exception.OutOfStockException;
import com.example.coursework.exception.ProductNotFoundException;
import com.example.coursework.exception.UserNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

@RestControllerAdvice
public class ProjectExceptionHandler {
    private static final String ERROR_PAGE = "errorPage";
    private static final String ERROR_MESSAGE = "errorMessage";

    @ExceptionHandler(ProductNotFoundException.class)
    public ModelAndView noProduct(final ProductNotFoundException ex) {
        return new ModelAndView(ERROR_PAGE).addObject(ERROR_MESSAGE, ex.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView userNotFound(final UserNotFoundException ex) {
        return new ModelAndView(ERROR_PAGE).addObject(ERROR_MESSAGE, ex.getMessage());
    }

    @ExceptionHandler(NegativeProductCountException.class)
    public ModelAndView negativeCount(final NegativeProductCountException ex) {
        return new ModelAndView(ERROR_PAGE).addObject(ERROR_MESSAGE, ex.getMessage());
    }

    @ExceptionHandler(OutOfStockException.class)
    public ModelAndView outOfStock(final OutOfStockException ex) {
        return new ModelAndView(ERROR_PAGE).addObject(ERROR_MESSAGE, ex.getMessage());
    }
}