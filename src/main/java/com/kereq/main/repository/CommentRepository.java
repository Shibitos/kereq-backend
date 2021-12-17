package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.CommentData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepository extends BaseRepository<CommentData> {

    Page<CommentData> findByPostId(Long postId, Pageable page);
}
