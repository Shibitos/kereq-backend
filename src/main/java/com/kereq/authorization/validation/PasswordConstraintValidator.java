package com.kereq.authorization.validation;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.kereq.authorization.validation.annotations.ValidPassword;
import org.passay.*;


public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public void initialize(final ValidPassword arg0) {

    }

    @Override
    public boolean isValid(final String password, final ConstraintValidatorContext context) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList(
        new LengthRule(8, 24),
        new CharacterRule(EnglishCharacterData.UpperCase, 1), // At least one upper-case character
        new CharacterRule(EnglishCharacterData.LowerCase, 1), // At least one lower-case character
        new CharacterRule(EnglishCharacterData.Digit, 1), // At least one digit character
        new CharacterRule(EnglishCharacterData.Special, 1), // At least one symbol (special character)
        new WhitespaceRule(), // No whitespace
        // rejects passwords that contain a sequence of >= 5 characters alphabetical  (e.g. abcdef)
        new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
        // rejects passwords that contain a sequence of >= 5 characters numerical   (e.g. 5)
        new IllegalSequenceRule(EnglishSequenceData.Numerical, 5, false)
        ));
        RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }
        List<String> messages = validator.getMessages(result);
        String messageTemplate = String.join(",", messages);
        context.buildConstraintViolationWithTemplate(messageTemplate)
                .addConstraintViolation()
                .disableDefaultConstraintViolation();
        
        return false;
    }
}
