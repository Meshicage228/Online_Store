package com.example.coursework.utils.validators;

import com.example.coursework.utils.annotations.CheckFileIsEmpty;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static java.util.Objects.isNull;

public class FileExistenceValidator implements ConstraintValidator<CheckFileIsEmpty, List<MultipartFile>> {
    @Override
    public boolean isValid(List<MultipartFile> value, ConstraintValidatorContext context) {
        if(isNull(value)) {
            return true;
        };
        return value.get(0).getSize() != 0;
    }
}
