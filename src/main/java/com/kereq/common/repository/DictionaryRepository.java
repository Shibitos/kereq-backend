package com.kereq.common.repository;

import com.kereq.common.entity.DictionaryData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DictionaryRepository extends JpaRepository<DictionaryData, Long> {

    boolean existsByCode(String code);
}
