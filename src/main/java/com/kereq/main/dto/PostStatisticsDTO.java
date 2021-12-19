package com.kereq.main.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostStatisticsDTO {

    private int likesCount;

    private int dislikesCount;

    private int commentsCount;

    private Integer userLikeType;
}
