package com.kereq.main.dto;

import com.kereq.common.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class ProfileImageDTO extends BaseDTO {

    @NotNull
    private MultipartFile file;

    @NotNull
    private Integer size;

    @NotNull
    private Integer posX;

    @NotNull
    private Integer posY;
}
