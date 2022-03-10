package com.kereq.common.service;

import com.kereq.common.constant.CacheName;
import com.kereq.common.entity.DictionaryItemData;
import com.kereq.common.error.RepositoryError;
import com.kereq.common.repository.DictionaryItemRepository;
import com.kereq.common.repository.DictionaryRepository;
import com.kereq.main.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictionaryService {

    private final DictionaryRepository dictionaryRepository;

    private final DictionaryItemRepository dictionaryItemRepository;

    public DictionaryService(DictionaryRepository dictionaryRepository, DictionaryItemRepository dictionaryItemRepository) {
        this.dictionaryRepository = dictionaryRepository;
        this.dictionaryItemRepository = dictionaryItemRepository;
    }

    @Cacheable(value = CacheName.DICTIONARY_ITEMS, key = "#code")
    public List<DictionaryItemData> getAllDictionaryItems(String code) {
        if (!dictionaryRepository.existsByCode(code)) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        return dictionaryItemRepository.findByDictionaryCode(code);
    }

    @Cacheable(value = CacheName.DICTIONARY_ITEMS, key = "#code")
    public DictionaryItemData getItemByCode(String code) {
        return dictionaryItemRepository.findByCode(code);
    }

    public boolean isItemInDictionary(String dictionaryCode, String code) {
        DictionaryItemData item = getItemByCode(code);
        return !(item == null || !item.getDictionary().getCode().equals(dictionaryCode));
    }
}
