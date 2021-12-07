package com.kereq.common.repository;

import com.kereq.common.entity.DictionaryItemData;

import java.util.List;

public interface DictionaryItemRepository extends BaseRepository<DictionaryItemData> {

    List<DictionaryItemData> findAllByDictionaryCode(String code);

    boolean existsByDictionaryCodeAndValue(String code, String value);
}
