package com.kereq.main.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.main.entity.PhotoData;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PhotoRepository extends BaseRepository<PhotoData> {

    PhotoData findByUserIdAndType(long userId, String type);

    Page<PhotoData> findByUserId(long userId, Pageable page);
}
