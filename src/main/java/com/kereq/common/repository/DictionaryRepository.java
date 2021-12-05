package com.kereq.common.repository;

import com.kereq.common.entity.DictionaryData;
import com.kereq.main.entity.UserData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DictionaryRepository extends JpaRepository<DictionaryData, Long> {

    boolean existsByCode(String code);
}
