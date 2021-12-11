package com.kereq.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class UserDTO {

    private Long id; //TODO: structure variant with modelmapper?
    private String firstName;
    private String lastName;
    private String email;
    private String country;
    private String birthDate;

    @JsonProperty("joinDate")
    private Date auditCD;

    private Set<RoleDTO> roles;
}
