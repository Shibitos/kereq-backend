package com.kereq.authorization.validation;

import com.kereq.authorization.dto.RegistrationDTO;
import com.kereq.authorization.validation.annotation.PasswordMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final RegistrationDTO user = (RegistrationDTO) obj;
        return Objects.equals(user.getPassword(), user.getConfirmPassword());
    }
}
