package com.kereq.common.repository;

import com.kereq.common.entity.DictionaryItemData;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DictionaryItemRepository extends JpaRepository<DictionaryItemData, Long> {

    List<DictionaryItemData> findAllByDictionaryCode(String code);

    boolean existsByDictionaryCodeAndValue(String code, String value);
}
