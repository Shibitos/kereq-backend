package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.PostStatisticsData;

public interface PostStatisticsRepository extends BaseRepository<PostStatisticsData> {

    boolean existsByPostId(Long postId);

    PostStatisticsData findByPostId(Long postId);
}
