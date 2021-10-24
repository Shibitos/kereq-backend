package com.kereq.messaging.repository;

import com.kereq.messaging.entity.MessageTemplateData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageTemplateRepository extends JpaRepository<MessageTemplateData, Long> {

    MessageTemplateData findByCode(String code);
}
