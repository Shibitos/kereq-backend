package com.kereq.authorization.dto;

import com.kereq.authorization.validation.annotation.PasswordMatch;
import com.kereq.authorization.validation.annotation.ValidPassword;
import com.kereq.common.constant.Dictionaries;
import com.kereq.common.validation.annotation.DictionaryValue;
import com.kereq.main.validation.annotation.ValidDate;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@PasswordMatch
public class RegistrationDTO { //TODO: validation messages?

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
    @DictionaryValue(code = Dictionaries.COUNTRIES)
    private String country;

    @NotNull
    @ValidDate
    private String birthDate;

    @NotNull
    @Size(min = 1, max = 1)
    private String gender;

    @NotNull
    @ValidPassword
    private String password;

    @NotNull
    private String confirmPassword;
}
