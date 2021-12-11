package com.kereq.common.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class DictionaryItemDTO {

    @NotNull
    @Size(min = 4, max = 25)
    private String code;

    @NotNull
    @Size(min = 4, max = 50)
    private String value;
}
