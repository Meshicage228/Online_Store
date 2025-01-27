package com.example.coursework.exceptions.handler;

import com.example.coursework.exceptions.*;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

@RestControllerAdvice
public class ProjectExceptionHandler {
    @ExceptionHandler(ProductNotFoundException.class)
    public ModelAndView noProduct(ProductNotFoundException ex){
        return new ModelAndView("errorPage").addObject("errorMessage", ex.getMessage());
    }
    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView userNotFound(UserNotFoundException ex){
        return new ModelAndView("errorPage").addObject("errorMessage", ex.getMessage());
    }
    @ExceptionHandler(NegativeProductCountException.class)
    public ModelAndView negativeCount(NegativeProductCountException ex){
        return new ModelAndView("errorPage").addObject("errorMessage", ex.getMessage());
    }
    @ExceptionHandler(OutOfStockException.class)
    public ModelAndView outOfStock(OutOfStockException ex){
        return new ModelAndView("errorPage").addObject("errorMessage", ex.getMessage());
    }
}
