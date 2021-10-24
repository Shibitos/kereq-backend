package com.kereq.messaging.repository;

import com.kereq.messaging.entity.MessageData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<MessageData, Long> {

}
