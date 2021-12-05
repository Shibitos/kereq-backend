package com.kereq.common.validation;

import com.kereq.common.validation.annotation.AllowedStrings;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

public class AllowedStringsValidator implements ConstraintValidator<AllowedStrings, String> {

    private String[] allowedValues;

    @Override
    public void initialize(AllowedStrings constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        allowedValues = constraintAnnotation.allowedValues();
    }

    @Override
    public boolean isValid(final String value, ConstraintValidatorContext constraintValidatorContext) {
        return Arrays.asList(allowedValues).contains(value);
    }
}
