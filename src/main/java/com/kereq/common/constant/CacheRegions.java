package com.kereq.common.constant;

import com.kereq.common.cache.CacheRegion;
import com.kereq.common.entity.BaseEntity;
import com.kereq.common.entity.DictionaryItemData;
import com.kereq.common.repository.DictionaryItemRepository;
import com.kereq.main.entity.RoleData;
import com.kereq.main.repository.RoleRepository;
import com.kereq.messaging.entity.MessageTemplateData;
import com.kereq.messaging.repository.MessageTemplateRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;

//public interface CacheRegions {
//    DICTIONARY_ITEMS(Names.DICTIONARY_ITEMS, DictionaryItemData.class, DictionaryItemRepository.class, true),
//    MESSAGE_TEMPLATES(Names.MESSAGE_TEMPLATES, MessageTemplateData.class, MessageTemplateRepository.class, true),
//    ROLES(Names.ROLES, RoleData.class, RoleRepository.class, true);
//
//    private String name;
//    private Class<? extends BaseEntity> entityClass;
//    private Class<? extends BaseRepository<?>> repositoryClass;
//    private boolean isPreloaded;
//
//    public <T> T getItem(ApplicationContext ctx, Long id) {
//        Cache cache = ctx.getBean(CacheManager.class).getCache(name);
//        if (cache == null) {
//            throw new ApplicationException(CommonError.OTHER_ERROR);
//        }
//        Cache.ValueWrapper item = cache.get(id);
//        return item != null ? (entityClass.cast(item.get())) : null;
//    }
//
//    public interface Names { //TODO: refactor
//
//        String DICTIONARY_ITEMS = "DICTIONARY_ITEMS";
//        String MESSAGE_TEMPLATES = "MESSAGE_TEMPLATES";
//        String ROLES = "ROLES";
//    }
//    CacheRegion<DictionaryItemData> DICTIONARY_ITEMS = new CacheRegion<>(Names.DICTIONARY_ITEMS, DictionaryItemRepository.class, true);
//    CacheRegion<MessageTemplateData> MESSAGE_TEMPLATES = new CacheRegion<>(Names.MESSAGE_TEMPLATES, MessageTemplateRepository.class, true);
//    CacheRegion<RoleData> ROLES = new CacheRegion<>(Names.ROLES, RoleRepository.class, true);
//
//    interface Names { //TODO: refactor
//
//        String DICTIONARY_ITEMS = "DICTIONARY_ITEMS";
//        String MESSAGE_TEMPLATES = "MESSAGE_TEMPLATES";
//        String ROLES = "ROLES";
//    }

//    @AllArgsConstructor
//    @Getter
//    enum Names {
//        DICTIONARY_ITEMS("DICTIONARY_ITEMS"),
//        MESSAGE_TEMPLATES("MESSAGE_TEMPLATES"),
//        ROLES("ROLES");
//
//        private String name;
//    }
//}

@AllArgsConstructor
@Getter
public enum CacheRegions {

    DICTIONARY_ITEMS(new CacheRegion<DictionaryItemData>(Names.DICTIONARY_ITEMS, DictionaryItemRepository.class, true)),
    MESSAGE_TEMPLATES(new CacheRegion<MessageTemplateData>(Names.MESSAGE_TEMPLATES, MessageTemplateRepository.class, true)),
    ROLES(new CacheRegion<RoleData>(Names.ROLES, RoleRepository.class, true));

    private CacheRegion<? extends BaseEntity> cacheRegion;

    public interface Names { //TODO: refactor

        String DICTIONARY_ITEMS = "DICTIONARY_ITEMS";
        String MESSAGE_TEMPLATES = "MESSAGE_TEMPLATES";
        String ROLES = "ROLES";
    }
}
