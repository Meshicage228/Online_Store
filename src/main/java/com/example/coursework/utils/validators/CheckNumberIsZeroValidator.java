package com.example.coursework.utils.validators;

import com.example.coursework.utils.annotations.CheckIsZero;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;

import static java.util.Objects.isNull;

@Component
public class CheckNumberIsZeroValidator implements ConstraintValidator<CheckIsZero, Float> {
    @Override
    public boolean isValid(Float value, ConstraintValidatorContext context) {
        return !isNull(value) && value != 0;
    }
}
