package com.kereq.common.config;

import com.kereq.common.cache.CacheRegion;
import com.kereq.common.constant.CacheProvider;
import com.kereq.common.constant.Dictionary;
import com.kereq.common.entity.BaseEntity;
import com.kereq.common.entity.CodeEntity;
import com.kereq.common.error.CommonError;
import com.kereq.common.repository.DictionaryItemRepository;
import com.kereq.main.exception.ApplicationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
public class CacheLoader implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(CacheLoader.class);

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
        logger.info("Loading caches");
        List<CacheProvider> cacheRegions = List.of(CacheProvider.values());
        for (CacheProvider ent : cacheRegions) {
            CacheRegion<?> region = ent.getCacheRegion();
            if (!region.isPreloaded()) {
                continue;
            }
            logger.info("Loading cache: {}", region.getName());
            for (Object obj : applicationContext.getBean(region.getRepositoryClass()).findAll()) {
                Cache cache = cacheManager.getCache(region.getName());
                if (cache == null) {
                    throw new ApplicationException(CommonError.OTHER_ERROR);
                }
                if (obj instanceof CodeEntity) {
                    cache.put(((CodeEntity) obj).getCode(), obj);
                }
                else if (obj instanceof BaseEntity) {
                    cache.put(((BaseEntity) obj).getId(), obj);
                }
            }
        }
        loadDictionariesLists();
        logger.info("All caches loaded");
    }

    private void loadDictionariesLists() {
        logger.info("Loading dictionaries lists");
        Cache cache = cacheManager.getCache(CacheProvider.CacheName.DICTIONARY_ITEMS);
        if (cache == null) {
            throw new ApplicationException(CommonError.OTHER_ERROR);
        }
        List<String> dictionaries = Arrays.stream(Dictionary.class.getFields())
                .map(Field::getName).collect(Collectors.toList());
        for (String dictionary : dictionaries) {
            logger.info("Loading dictionary list: {}", dictionary);
            cache.put(dictionary, dictionaryItemRepository.findByDictionaryCode(dictionary));
        }
        logger.info("All dictionaries lists loaded");
    }
}
