package com.kereq.common.repository;

import com.kereq.common.entity.DictionaryData;

public interface DictionaryRepository extends BaseRepository<DictionaryData> {

    boolean existsByCode(String code);
}
