package com.kereq.common.cache;

import com.kereq.common.entity.BaseEntity;
import com.kereq.common.error.CommonError;
import com.kereq.common.repository.BaseRepository;
import com.kereq.main.exception.ApplicationException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;

import javax.persistence.EntityManager;

@AllArgsConstructor
@Getter
public class CacheRegion<T extends BaseEntity> {
    private final String name;
    private final Class<? extends BaseRepository<?>> repositoryClass;
    private final boolean isPreloaded;

    @SuppressWarnings("unchecked")
    public T getItem(ApplicationContext ctx, long id) {
        Cache.ValueWrapper item = getCache(ctx).get(id);
        return item != null ? (T) item.get() : null;
    }

    @SuppressWarnings("unchecked")
    public T getItem(ApplicationContext ctx, String code) {
        Cache.ValueWrapper item = getCache(ctx).get(code);
        return item != null ? (T) item.get() : null;
    }

    public T getItemAttached(ApplicationContext ctx, long id) {
        T item = getItem(ctx, id);
        if (item != null) {
            ctx.getBean(EntityManager.class).merge(item);
            return item;
        }
        return null;
    }

    public T getItemAttached(ApplicationContext ctx, String code) {
        T item = getItem(ctx, code);
        if (item != null) {
            return ctx.getBean(EntityManager.class).merge(item);
        }
        return null;
    }

    private Cache getCache(ApplicationContext ctx) {
        Cache cache = ctx.getBean(CacheManager.class).getCache(name);
        if (cache == null) {
            throw new ApplicationException(CommonError.OTHER_ERROR);
        }
        return cache;
    }
}
