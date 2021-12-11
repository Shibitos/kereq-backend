package com.kereq.main.validation;

import com.kereq.main.util.DateUtil;
import com.kereq.main.validation.annotation.ValidDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateValidator implements ConstraintValidator<ValidDate, Object> {

    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSX";

    private String format;
    private String messageFormat;
    private boolean allowFuture;
    private String messagePast;

    @Override
    public void initialize(ValidDate constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        format = constraintAnnotation.format();
        messageFormat = constraintAnnotation.messageFormat();
        allowFuture = constraintAnnotation.allowFuture();
        messagePast = constraintAnnotation.messagePast();
    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final String dateStr = (String) obj;
        DateFormat sdf = new SimpleDateFormat(format);
        sdf.setLenient(false);
        Date date;
        try {
            date = sdf.parse(dateStr);
        } catch (Exception e) {
            context.buildConstraintViolationWithTemplate(messageFormat + format)
                    .addConstraintViolation().disableDefaultConstraintViolation();
            return false;
        }
        if (!allowFuture && date.after(DateUtil.now())) {
            context.buildConstraintViolationWithTemplate(messagePast)
                    .addConstraintViolation().disableDefaultConstraintViolation();
            return false;
        }
        return true;
    }
}
