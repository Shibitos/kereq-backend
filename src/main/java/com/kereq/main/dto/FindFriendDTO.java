package com.kereq.main.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FindFriendDTO {

    private UserDTO user;
    private Integer minAge;
    private Integer maxAge;
    private String gender;
}
