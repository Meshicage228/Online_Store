package com.example.coursework.utils.annotations;


import com.example.coursework.utils.validators.CheckNumberIsZeroValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(FIELD)
@Constraint(validatedBy = CheckNumberIsZeroValidator.class)
public @interface CheckIsZero {
    String message() default "Цена не может быть равна 0";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
