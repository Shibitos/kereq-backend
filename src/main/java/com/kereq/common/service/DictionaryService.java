package com.kereq.common.service;

import com.kereq.common.constant.CacheRegions;
import com.kereq.common.entity.DictionaryItemData;
import com.kereq.common.repository.DictionaryItemRepository;
import com.kereq.common.repository.DictionaryRepository;
import com.kereq.main.error.RepositoryError;
import com.kereq.main.exception.ApplicationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DictionaryService {

    @Autowired
    private DictionaryRepository dictionaryRepository;

    @Autowired
    private DictionaryItemRepository dictionaryItemRepository;

    @Autowired
    private DictionaryService self; //TODO: aspecj? spring aop?

    public List<DictionaryItemData> getAllDictionaryValues(String code) { //TODO: cache
        if (!dictionaryRepository.existsByCode(code)) {
            throw new ApplicationException(RepositoryError.RESOURCE_NOT_FOUND);
        }
        return dictionaryItemRepository.findAllByDictionaryCode(code);
    }

    @Cacheable(value = CacheRegions.Names.DICTIONARY_ITEMS, key="#code")
    public DictionaryItemData getItemByCode(String code) {
        return null;
    }

    public boolean isItemInDictionary(String dictionaryCode, String code) {
        DictionaryItemData item = self.getItemByCode(code);
        return !(item == null || !item.getDictionary().getCode().equals(dictionaryCode));
    }
}
