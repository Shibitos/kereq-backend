package com.kereq.authorization.validation;

import com.kereq.authorization.dto.UserDTO;
import com.kereq.authorization.validation.annotations.PasswordMatch;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchValidator implements ConstraintValidator<PasswordMatch, Object> {

    @Override
    public void initialize(final PasswordMatch constraintAnnotation) {

    }

    @Override
    public boolean isValid(final Object obj, final ConstraintValidatorContext context) {
        final UserDTO user = (UserDTO) obj;
        return user.getPassword().equals(user.getConfirmPassword());
    }
}
