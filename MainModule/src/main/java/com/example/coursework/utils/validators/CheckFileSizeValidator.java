package com.example.coursework.utils.validators;

import com.example.coursework.utils.annotations.CheckFileSize;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static java.util.Objects.isNull;

@Component
public class CheckFileSizeValidator implements ConstraintValidator<CheckFileSize, List<MultipartFile>> {
    private final long MB = (long) Math.pow(1024, 2);

    private long maxSize;

    @Override
    public void initialize(CheckFileSize constraintAnnotation) {
        this.maxSize = constraintAnnotation.maxSizeInMB();
    }

    @Override
    public boolean isValid(List<MultipartFile> value, ConstraintValidatorContext context) {
        if(isNull(value)) return true;
        for(var file : value){
            if(file.getSize() == 0) return  true;
            if(file.getSize() > MB * maxSize){
                return false;
            }
        }
        return true;
    }
}
