package com.kereq.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kereq.common.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class UserDTO extends BaseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String country; //TODO: val instead of code?
    private String birthDate;

    @JsonProperty("joinDate")
    private Date auditCD;

    private Set<RoleDTO> roles;
}
