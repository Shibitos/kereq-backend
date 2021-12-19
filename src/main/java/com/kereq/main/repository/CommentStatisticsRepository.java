package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.CommentStatisticsData;

public interface CommentStatisticsRepository extends BaseRepository<CommentStatisticsData> {

    boolean existsByCommentId(Long postId);

    CommentStatisticsData findByCommentId(Long postId);
}
