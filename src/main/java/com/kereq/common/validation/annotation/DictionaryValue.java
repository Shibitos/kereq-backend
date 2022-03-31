package com.kereq.common.validation.annotation;

import com.kereq.common.validation.DictionaryValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Documented
@Constraint(validatedBy = DictionaryValidator.class)
@Target({TYPE, FIELD, ANNOTATION_TYPE})
@Retention(RUNTIME)
public @interface DictionaryValue {
    String message() default "Unallowed value.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String code();
}
