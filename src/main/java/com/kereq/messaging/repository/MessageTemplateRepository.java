package com.kereq.messaging.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.messaging.entity.MessageTemplateData;

public interface MessageTemplateRepository extends BaseRepository<MessageTemplateData> {

    MessageTemplateData findByCode(String code);
}
