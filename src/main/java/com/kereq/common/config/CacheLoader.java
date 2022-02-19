package com.kereq.common.config;

import com.kereq.common.cache.CacheRegion;
import com.kereq.common.constant.CacheName;
import com.kereq.common.constant.CacheProvider;
import com.kereq.common.constant.Dictionary;
import com.kereq.common.entity.BaseEntity;
import com.kereq.common.entity.CodeEntity;
import com.kereq.common.error.CommonError;
import com.kereq.common.repository.DictionaryItemRepository;
import com.kereq.main.exception.ApplicationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CacheLoader implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    CacheManager cacheManager;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DictionaryItemRepository dictionaryItemRepository;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        loadCaches();
    }

    private void loadCaches() {
        log.info("Loading caches");
        List<CacheProvider> cacheRegions = List.of(CacheProvider.values());
        for (CacheProvider ent : cacheRegions) {
            CacheRegion<?> region = ent.getCacheRegion();
            if (!region.isPreloaded()) {
                continue;
            }
            log.info("Loading cache: {}", region.getName());
            for (Object obj : applicationContext.getBean(region.getRepositoryClass()).findAll()) {
                Cache cache = cacheManager.getCache(region.getName());
                if (cache == null) {
                    throw new ApplicationException(CommonError.OTHER_ERROR);
                }
                if (obj instanceof CodeEntity) {
                    cache.put(((CodeEntity) obj).getCode(), obj);
                } else if (obj instanceof BaseEntity) {
                    cache.put(((BaseEntity) obj).getId(), obj);
                }
            }
        }
        loadDictionariesLists();
        log.info("All caches loaded");
    }

    private void loadDictionariesLists() {
        log.info("Loading dictionaries lists");
        Cache cache = cacheManager.getCache(CacheName.DICTIONARY_ITEMS);
        if (cache == null) {
            throw new ApplicationException(CommonError.OTHER_ERROR);
        }
        List<String> dictionaries = Arrays.stream(Dictionary.class.getFields())
                .map(Field::getName).collect(Collectors.toList());
        for (String dictionary : dictionaries) {
            log.info("Loading dictionary list: {}", dictionary);
            cache.put(dictionary, dictionaryItemRepository.findByDictionaryCode(dictionary));
        }
        log.info("All dictionaries lists loaded");
    }
}
