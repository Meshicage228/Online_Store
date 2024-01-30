package com.example.coursework.utils.validators;

import com.example.coursework.utils.annotations.CheckFileSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class CheckFileSizeValidator implements ConstraintValidator<CheckFileSize, MultipartFile> {
    private final long MB = (long) Math.pow(1024, 2);

    private long maxSize;

    @Override
    public void initialize(CheckFileSize constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSizeInMB();
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        return value.getSize() < maxSize * MB;
    }
}
