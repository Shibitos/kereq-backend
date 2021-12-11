package com.kereq.common.config;

import com.kereq.common.cache.CacheRegion;
import com.kereq.common.constant.CacheRegions;
import com.kereq.common.entity.BaseEntity;
import com.kereq.common.entity.CodeEntity;
import com.kereq.main.error.CommonError;
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

import java.util.List;

@Service
public class CacheLoader implements ApplicationListener<ApplicationReadyEvent> {

    private static final Logger logger = LoggerFactory.getLogger(CacheLoader.class);

    @Autowired
    CacheManager cacheManager;

    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent applicationReadyEvent) {
        loadCaches();
    }

    private void loadCaches() {
        logger.info("Loading caches");
        List<CacheRegions> cacheRegions = List.of(CacheRegions.values());
        for (CacheRegions ent : cacheRegions) {
            CacheRegion<?> region = ent.getCacheRegion();
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
        logger.info("All caches loaded");
    }
}
