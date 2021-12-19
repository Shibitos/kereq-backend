package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.PostLikeData;

public interface PostLikeRepository extends BaseRepository<PostLikeData> {

    PostLikeData findByUserIdAndPostId(Long userId, Long postId);
}
