package com.kereq.authorization.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class LoginDTO {

    @NotNull
    @Size(min = 4, max = 25)
    private String login;

    @NotNull
    private String password;
}
