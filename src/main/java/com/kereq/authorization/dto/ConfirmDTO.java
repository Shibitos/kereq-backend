package com.kereq.authorization.dto;

import com.kereq.common.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ConfirmDTO extends BaseDTO {

    @NotNull
    @Size(min = 36, max = 36)
    private String token;
}
