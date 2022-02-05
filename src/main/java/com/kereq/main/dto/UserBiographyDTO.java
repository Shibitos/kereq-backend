package com.kereq.main.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class UserBiographyDTO {

    @Size(max = 200)
    private String biography;
}
