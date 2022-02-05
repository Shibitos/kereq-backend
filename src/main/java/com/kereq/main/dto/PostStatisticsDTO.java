package com.kereq.main.dto;

import com.kereq.common.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostStatisticsDTO extends BaseDTO {

    private int likesCount;

    private int dislikesCount;

    private int commentsCount;

    private Integer userLikeType;
}
