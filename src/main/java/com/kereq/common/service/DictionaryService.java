package com.kereq.common.service;

import com.kereq.common.entity.DictionaryItemData;
import com.kereq.common.repository.DictionaryItemRepository;
import com.kereq.common.repository.DictionaryRepository;
import com.kereq.main.error.RepositoryError;
import com.kereq.main.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictionaryService {

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private DictionaryItemRepository dictionaryItemRepository;

    public List<DictionaryItemData> getAllDictionaryValues(String code) { //TODO: cache
        if (!dictionaryRepository.existsByCode(code)) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        return dictionaryItemRepository.findAllByDictionaryCode(code);
    }

    public boolean isItemInDictionary(String code, String value) {
        if (!dictionaryRepository.existsByCode(code)) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        return dictionaryItemRepository.existsByDictionaryCodeAndValue(code, value);
    }
}
