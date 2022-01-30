package com.kereq.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kereq.common.constant.Dictionary;
import com.kereq.common.dto.BaseDTO;
import com.kereq.common.validation.annotation.DictionaryValue;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.Set;

@Getter
@Setter
public class UserDTO extends BaseDTO {

    private Long id;

    @NotNull
    @Size(min = 4, max = 25)
    private String firstName;

    @NotNull
    @Size(min = 4, max = 25)
    private String lastName;

    private String email;

    @NotNull
    @DictionaryValue(code = Dictionary.COUNTRIES)
    private String country; //TODO: val instead of code in front

    private String birthDate;

    private String biography;

    @JsonProperty("joinDate")
    private Date auditCD;

    private Set<RoleDTO> roles;
}
