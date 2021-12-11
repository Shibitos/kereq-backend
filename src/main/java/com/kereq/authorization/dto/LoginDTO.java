package com.kereq.authorization.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class LoginDTO {

    @NotNull
    @Size(min = 8, max = 50)
    private String email;

    @NotNull
    private String password;
}
