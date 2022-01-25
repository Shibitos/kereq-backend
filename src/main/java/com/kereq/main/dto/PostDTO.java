package com.kereq.main.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kereq.common.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class PostDTO extends BaseDTO {

    private Long id;

    private UserDTO user; //TODO: only for browse

    @JsonProperty("createdAt")
    private Date auditCD;

    @Size(min = 15, max = 1000)
    private String content;

    List<CommentDTO> comments;

    PostStatisticsDTO statistics;
}
