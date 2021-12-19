package com.kereq.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.Date;

@Getter
@Setter
public class CommentDTO {

    private Long id;

    private UserDTO user; //TODO: only for browse

    @JsonProperty("createdAt")
    private Date auditCD;

    @Size(min = 1, max = 1000)
    private String content;

    private CommentStatisticsDTO statistics;
}
