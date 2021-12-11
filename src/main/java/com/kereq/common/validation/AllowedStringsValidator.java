package com.kereq.common.validation;

import com.kereq.common.validation.annotation.AllowedStrings;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.regex.Pattern;

public class AllowedStringsValidator implements ConstraintValidator<AllowedStrings, String> {

    private String[] allowedValues;
    private String delimiter;

    @Override
    public void initialize(AllowedStrings constraintAnnotation) {
        allowedValues = constraintAnnotation.allowedValues();
        delimiter = constraintAnnotation.delimiter();
    }

    @Override
    public boolean isValid(final String value, ConstraintValidatorContext constraintValidatorContext) {
        if (delimiter.length() > 0) {
            return Arrays.stream(allowedValues)
                    .anyMatch(v -> Arrays.asList(v.split(Pattern.quote(delimiter))).contains(value)); //TODO: without regex?
        }
        return Arrays.asList(allowedValues).contains(value);
    }
}
