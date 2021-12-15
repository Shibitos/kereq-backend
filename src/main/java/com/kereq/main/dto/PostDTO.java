package com.kereq.main.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class PostDTO {

    private UserDTO user; //TODO: only for browse

    @Size(min = 15, max = 1000)
    private String content;
}
