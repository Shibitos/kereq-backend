package com.kereq.unit;

import com.kereq.main.validation.DateValidator;
import com.kereq.main.validation.annotation.ValidDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.validation.ConstraintValidatorContext;
import javax.validation.Payload;
import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class DateValidatorUnitTest {

    private DateValidator dateValidator = new DateValidator();

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(context.buildConstraintViolationWithTemplate(Mockito.any())).thenReturn(constraintViolationBuilder);
        when(constraintViolationBuilder.addConstraintViolation()).thenReturn(constraintValidatorContext);
    }

    @Test
    void testIsValidDefaultFormat() {
        final String[] validDefaultDates = {
                "2015-12-29T23:59:59.999+00:00",
                "2015-12-29T23:59:59.999+12:00",
                "2015-01-01T12:00:00.000+01:00",
                "2018-09-15T00:53:00.000-05:00",
                "2016-09-18T17:34:02.666Z"
        };
        final String[] invalidDefaultDates = {
                "201x-12-29T23:59:59.999+12:00",
                "2015-01-01T12:00:00.000+23:00",
                "20151-01-01T12:00:00.000+01:00",
                "2018-13-15T00:53:00.000-05:00",
                "2016-09-35T17:34:02.666Z",
                "2016-0x-35T17:34:02.666Z",
                "2016-09-3xT17:34:02.666Z",
                "2016-09-35E17:34:02.666Z",
                "2016-09-35T1x:34:02.666Z",
                "2016-09-35T11:3x:02.666Z",
                "2016-09-35T17:34:0x.666Z",
                "2016-09-35T17:34:02x666Z"
        };
        final String futureDefaultDate = "2518-12-29T23:59:59.999+00:00";
        dateValidator.initialize(buildValidDate(DateValidator.DATE_FORMAT, false));
        for (String date : validDefaultDates) {
            assertThat(dateValidator.isValid(date, context)).isTrue();
        }
        for (String date : invalidDefaultDates) {
            assertThat(dateValidator.isValid(date, context)).isFalse();
        }
        assertThat(dateValidator.isValid(futureDefaultDate, context)).isFalse();
        dateValidator.initialize(buildValidDate(DateValidator.DATE_FORMAT, true));
        assertThat(dateValidator.isValid(futureDefaultDate, context)).isTrue();
    }

    @Test
    void testCustomFormat() {
        final String format = "yyyy-MM-dd";
        final String[] validDates = {
                "2015-12-29",
                "2002-07-25",
                "2007-03-01",
                "2015-12-29T23:59:59.999+00:00",
                "2015-12-29T23:59:59.999+12:00"
        };
        final String[] invalidDates = {
                "29-12-2015T23:59:59.999+00:00",
                "2015-29-12T23:59:59.999+12:00",
                "2002-13-01",
                "2001_01_01"
        };
        dateValidator.initialize(buildValidDate(format, false));
        for (String date : validDates) {
            assertThat(dateValidator.isValid(date, context)).isTrue();
        }
        for (String date : invalidDates) {
            assertThat(dateValidator.isValid(date, context)).isFalse();
        }
    }

    private ValidDate buildValidDate(String format, boolean allowFuture) {
        return new ValidDate() {

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @Override
            public String message() {
                return null;
            }

            @Override
            public String messageFormat() {
                return "";
            }

            @Override
            public String messagePast() {
                return null;
            }

            @Override
            public Class<?>[] groups() {
                return new Class[0];
            }

            @Override
            public Class<? extends Payload>[] payload() {
                return new Class[0];
            }

            @Override
            public String format() {
                return format;
            }

            @Override
            public boolean allowFuture() {
                return allowFuture;
            }
        };
    }
}
