package com.example.coursework.utils.annotations;

import com.example.coursework.utils.validators.CheckFileSizeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({TYPE_USE, FIELD})
@Constraint(validatedBy = CheckFileSizeValidator.class)
public @interface CheckFileSize {
    String message() default "Превышен допустимый размер файла";

    Class<?>[] groups() default { };

    long maxSizeInMB () default 10;

    Class<? extends Payload>[] payload() default { };
}
