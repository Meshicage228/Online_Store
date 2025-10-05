package com.example.coursework.utils.annotations;

import com.example.coursework.utils.validators.FileExistenceValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE_USE, FIELD})
@Constraint(validatedBy = FileExistenceValidator.class)
public @interface CheckFileIsEmpty {
    String message() default "Выберите 1 или более изображений";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };
}
