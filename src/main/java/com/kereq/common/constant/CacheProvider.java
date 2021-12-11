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

@AllArgsConstructor
@Getter
public enum CacheProvider {

    DICTIONARY_ITEMS(new CacheRegion<DictionaryItemData>(CacheName.DICTIONARY_ITEMS, DictionaryItemRepository.class, true)),
    MESSAGE_TEMPLATES(new CacheRegion<MessageTemplateData>(CacheName.MESSAGE_TEMPLATES, MessageTemplateRepository.class, true)),
    ROLES(new CacheRegion<RoleData>(CacheName.ROLES, RoleRepository.class, true));

    private CacheRegion<? extends BaseEntity> cacheRegion;

    public interface CacheName { //TODO: refactor

        String DICTIONARY_ITEMS = "DICTIONARY_ITEMS";
        String MESSAGE_TEMPLATES = "MESSAGE_TEMPLATES";
        String ROLES = "ROLES";
    }
}
