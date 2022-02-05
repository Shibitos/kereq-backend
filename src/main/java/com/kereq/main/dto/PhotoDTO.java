package com.kereq.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kereq.common.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class PhotoDTO extends BaseDTO {

    @JsonProperty("photoId")
    private String uuid;

    @JsonProperty("createdAt")
    private Date auditCD;
}
