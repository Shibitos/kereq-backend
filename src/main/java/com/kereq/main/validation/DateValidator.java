package com.kereq.main.validation;

import com.kereq.main.validation.annotation.ValidDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class DateValidator implements ConstraintValidator<ValidDate, Object> {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final String dateStr = (String) obj;
        DateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        sdf.setLenient(false);
        try {
            sdf.parse(dateStr);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
