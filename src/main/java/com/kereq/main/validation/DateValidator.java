package com.kereq.main.validation;

import com.kereq.main.validation.annotation.ValidDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateValidator implements ConstraintValidator<ValidDate, Object> {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    private String format;
    private String message;

    @Override
    public void initialize(ValidDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        format = constraintAnnotation.format();
        message = constraintAnnotation.message();
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final String dateStr = (String) obj;
        DateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (Exception e) {
            context.buildConstraintViolationWithTemplate(message + format)
                    .addConstraintViolation().disableDefaultConstraintViolation();
            return false;
        }
        return true;
    }
}
