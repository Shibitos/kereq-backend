package com.kereq.authorization.dto;

import com.kereq.common.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class ResendTokenDTO extends BaseDTO {

    @NotNull
    @Size(min = 4, max = 50)
    private String email;
}
