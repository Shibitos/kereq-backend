package com.kereq.authorization.dto;

import com.kereq.authorization.validation.annotation.PasswordMatch;
import com.kereq.authorization.validation.annotation.ValidPassword;
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
    @Size(min = 4, max = 30)
    private String country;

    @NotNull
    @ValidPassword
    private String password;

    @NotNull
    private String confirmPassword;
}
