package com.kereq.common.config;

import com.kereq.common.constant.CacheName;
import com.kereq.common.constant.CacheProvider;
import org.ehcache.config.CacheConfiguration;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.MemoryUnit;
import org.ehcache.core.config.DefaultConfiguration;
import org.ehcache.jsr107.EhcacheCachingProvider;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.AdviceMode;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableCaching(mode = AdviceMode.ASPECTJ)
public class CacheConfig extends CachingConfigurerSupport {

    private static final long DEFAULT_CACHE_MB_SIZE = 10L;

    @Override
    @Bean
    public org.springframework.cache.CacheManager cacheManager() {
        return new JCacheCacheManager(createInMemoryCacheManager());
    }

    private CacheManager createInMemoryCacheManager() {
        org.ehcache.config.CacheConfiguration<Object, Object> cacheConfiguration = CacheConfigurationBuilder
                .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder
                        .newResourcePoolsBuilder().offheap(DEFAULT_CACHE_MB_SIZE, MemoryUnit.MB))
                .build();
        Map<String, CacheConfiguration<?, ?>> caches = createCacheConfigurations(cacheConfiguration);

        EhcacheCachingProvider provider = getCachingProvider();
        DefaultConfiguration configuration = new DefaultConfiguration(caches, getClassLoader());
        return getCacheManager(provider, configuration);
    }

    private Map<String, org.ehcache.config.CacheConfiguration<?, ?>> createCacheConfigurations(
            org.ehcache.config.CacheConfiguration<Object, Object> cacheConfiguration) {
        return Arrays.stream(CacheName.class.getFields())
                .collect(Collectors.toMap(Field::getName, field -> cacheConfiguration));
    }

    private EhcacheCachingProvider getCachingProvider() {
        return (EhcacheCachingProvider) Caching.getCachingProvider();
    }

    private ClassLoader getClassLoader() {
        return this.getClass().getClassLoader();
    }

    private CacheManager getCacheManager(EhcacheCachingProvider provider, DefaultConfiguration configuration) {
        return provider.getCacheManager(provider.getDefaultURI(), configuration);
    }
}
