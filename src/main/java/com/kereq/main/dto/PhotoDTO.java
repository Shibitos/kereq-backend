package com.kereq.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kereq.common.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class PhotoDTO extends BaseDTO {

    private String photoId;

    @JsonProperty("createdAt")
    private Date auditCD;

    public void fillPhotoId(UUID uuid) {
        setPhotoId(uuid.toString().replace("-", ""));
    }
}
