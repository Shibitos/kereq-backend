package com.kereq.main.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentStatisticsDTO {

    private int likesCount;

    private int dislikesCount;

    private Integer userLikeType;
}
