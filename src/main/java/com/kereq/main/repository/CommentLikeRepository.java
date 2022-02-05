package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.CommentLikeData;

public interface CommentLikeRepository extends BaseRepository<CommentLikeData> {

    CommentLikeData findByUserIdAndCommentId(long userId, long commentId);
}
