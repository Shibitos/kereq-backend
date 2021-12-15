package com.kereq.main.dto;

import com.kereq.common.constant.Gender;
import com.kereq.common.validation.annotation.AllowedStrings;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;

@Getter
@Setter
public class FindFriendDTO {

    private UserDTO user; //TODO: only for browse
    private Integer minAge;
    private Integer maxAge; //TODO: validation min<=max

    @AllowedStrings(allowedValues = {Gender.MALE, Gender.FEMALE}, nullable = true)
    private String gender;

    @Size(min = 15, max = 400)
    private String description;
}
