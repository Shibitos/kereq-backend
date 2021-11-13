package com.kereq.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class UserDTO {

    private String login;
    private String firstName;
    private String lastName;
    private String email;
    private String country;

    @JsonProperty("joinDate")
    private Date auditCD;

    private Set<RoleDTO> roles;
}
