package com.kereq.authorization.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kereq.authorization.validation.annotations.PasswordMatch;
import com.kereq.authorization.validation.annotations.ValidPassword;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
@PasswordMatch
public class UserDTO {

    @NotNull
    @Size(min = 4, max = 25)
    private String login;

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
    @ValidPassword
    private String password;

    @NotNull
    private String confirmPassword;
}
