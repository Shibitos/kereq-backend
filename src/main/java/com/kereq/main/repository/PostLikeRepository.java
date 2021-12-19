package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.CommentData;
import com.kereq.main.entity.PostLikeData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostLikeRepository extends BaseRepository<PostLikeData> {

    PostLikeData findByUserIdAndPostId(Long userId, Long postId);
}
