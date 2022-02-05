package com.kereq.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kereq.common.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class FriendshipDTO extends BaseDTO {

    private UserDTO user;
    private UserDTO friend;

    @JsonProperty("startDate")
    private Date auditMD;
}
