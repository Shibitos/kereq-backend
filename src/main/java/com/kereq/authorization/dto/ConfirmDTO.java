package com.kereq.authorization.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ConfirmDTO {

    @NotNull
    @Size(min = 36, max = 36)
    private String token;
}
