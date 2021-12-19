package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.CommentData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends BaseRepository<CommentData> {

    @Query(countQuery = "SELECT ps.commentsCount FROM PostStatisticsData ps WHERE ps.postId = :postId")
    Page<CommentData> findByPostId(Long postId, Pageable page);
}
