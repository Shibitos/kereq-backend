package com.kereq.authorization.validation;

import com.kereq.authorization.validation.annotation.ValidPassword;
import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class PasswordConstraintValidator implements ConstraintValidator<ValidPassword, String> {

    @Override
    public boolean isValid(final String password, final ConstraintValidatorContext context) {
        PasswordValidator validator = new PasswordValidator(Arrays.asList( //TODO: think of it
        new LengthRule(8, 24),
        new CharacterRule(EnglishCharacterData.UpperCase, 1), //At least one upper-case character
        new CharacterRule(EnglishCharacterData.LowerCase, 1), //At least one lower-case character
        new CharacterRule(EnglishCharacterData.Digit, 1), //At least one digit character
        new CharacterRule(EnglishCharacterData.Special, 1), //At least one symbol (special character)
        new WhitespaceRule(), //No whitespace
        //Rejects passwords that contain a sequence of >= 5 characters alphabetical  (e.g. abcdef)
        new IllegalSequenceRule(EnglishSequenceData.Alphabetical, 5, false),
        //Rejects passwords that contain a sequence of >= 5 characters numerical   (e.g. 5)
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
