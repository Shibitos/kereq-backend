package com.kereq.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kereq.common.dto.BaseDTO;
import com.kereq.main.entity.PhotoData;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class PhotoDTO extends BaseDTO {

    @JsonProperty("photoId")
    private String uuid;

    @JsonProperty("createdAt")
    private Date auditCD;
}
