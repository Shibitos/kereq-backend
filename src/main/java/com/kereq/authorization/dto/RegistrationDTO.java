package com.kereq.authorization.dto;

import com.kereq.authorization.validation.annotation.PasswordMatch;
import com.kereq.authorization.validation.annotation.ValidPassword;
import com.kereq.common.constant.Dictionary;
import com.kereq.common.constant.Gender;
import com.kereq.common.dto.BaseDTO;
import com.kereq.common.validation.annotation.AllowedStrings;
import com.kereq.common.validation.annotation.DictionaryValue;
import com.kereq.common.validation.annotation.ValidDate;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@PasswordMatch
public class RegistrationDTO extends BaseDTO { //TODO: validation messages?

    @NotNull
    @Size(min = 4, max = 25)
    private String firstName;

    @NotNull
    @Size(min = 4, max = 25)
    private String lastName;

    @NotNull
    @Size(min = 8, max = 50)
    @Email
    private String email;

    @NotNull
    @DictionaryValue(code = Dictionary.COUNTRIES)
    private String country;

    @NotNull
    @ValidDate(format = "yyyy-MM-dd", allowFuture = false)
    private String birthDate;

    @NotNull
    @AllowedStrings(allowedValues = {Gender.MALE, Gender.FEMALE})
    private String gender;

    @NotNull
    @ValidPassword
    @Size(min = 8, max = 24)
    private String password;

    @NotNull
    private String confirmPassword;
}
