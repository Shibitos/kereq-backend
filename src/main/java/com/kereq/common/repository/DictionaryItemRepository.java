package com.kereq.common.repository;

import com.kereq.common.entity.DictionaryItemData;

import java.util.List;

public interface DictionaryItemRepository extends BaseRepository<DictionaryItemData> {

    List<DictionaryItemData> findAllByDictionaryCode(String code);

    DictionaryItemData findByCode(String code);

    boolean existsByDictionaryCodeAndCode(String dictionaryCode, String code);
}
