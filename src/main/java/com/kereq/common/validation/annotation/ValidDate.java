package com.kereq.common.validation.annotation;

import com.kereq.common.validation.DateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = DateValidator.class)
@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface ValidDate {

    String message() default "Invalid date.";

    String messageFormat() default "Invalid date format. Expected: ";

    String messagePast() default "Date must be in the past";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String format() default DateValidator.DATE_FORMAT;

    boolean allowFuture() default true;
}
