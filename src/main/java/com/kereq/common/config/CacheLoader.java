package com.kereq.common.config;

import com.kereq.common.repository.BaseRepository;
import com.kereq.common.repository.DictionaryRepository;
import com.kereq.main.repository.RoleRepository;
import com.kereq.messaging.repository.MessageTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class CacheLoader implements ApplicationListener<ApplicationReadyEvent> {

    private final Logger logger = LoggerFactory.getLogger(CacheLoader.class);

    @Autowired
    CacheManager cacheManager;

    @Autowired
    private ApplicationContext applicationContext;

    private final static Map<String, Class<? extends BaseRepository<?>>> caches;
    static {
        caches = new LinkedHashMap<>();
        addCache("DICTIONARIES", DictionaryRepository.class);
        addCache("MESSAGE_TEMPLATES", MessageTemplateRepository.class);
        addCache("ROLES", RoleRepository.class);
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        loadCaches();
    }

    public static List<String> getCacheNames() {
        return new ArrayList<>(caches.keySet());
    }

    private void loadCaches() {
        logger.info("Loading caches");
        for (Map.Entry<String, Class<? extends BaseRepository<?>>> entry : caches.entrySet()) {
            logger.info("Loading cache: {}", entry.getKey());
            for (Object obj : applicationContext.getBean(entry.getValue()).findAll()) {
                //cacheManager.getCache(entry.getKey()).put(obj, obj); //TODO: key
            }
        }
        logger.info("All caches loaded");
    }

    private static void addCache(String name, Class<? extends BaseRepository<?>> repositoryClass) {
        caches.put(name, repositoryClass);
    }
}
