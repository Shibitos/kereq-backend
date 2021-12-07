package com.kereq.common.validation;

import com.kereq.common.service.DictionaryService;
import com.kereq.common.validation.annotation.DictionaryValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class DictionaryValidator implements ConstraintValidator<DictionaryValue, String> {

    private String dictCode;

    @Autowired
    private DictionaryService dictionaryService;

    @Override
    public void initialize(DictionaryValue constraintAnnotation) {
        dictCode = constraintAnnotation.code();
    }

    @Override
    public boolean isValid(final String value, ConstraintValidatorContext constraintValidatorContext) {
        return dictionaryService.isItemInDictionary(dictCode, value);
    }
}
