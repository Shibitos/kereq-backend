package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.PostStatisticsData;

public interface PostStatisticsRepository extends BaseRepository<PostStatisticsData> {

    boolean existsByPostId(long postId);

    PostStatisticsData findByPostId(long postId);
}
