package com.kereq.messaging.repository;

import com.kereq.common.repository.BaseRepository;
import com.kereq.messaging.entity.MessageData;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends BaseRepository<MessageData> {

    @Query(value = "SELECT * FROM MESSAGES, MESSAGE_TEMPLATES WHERE MSG_MSG_TMP_ID=MSG_TMP_ID" +
            " MSG_TO=?1 AND MSG_TMP_CODE=?2 ORDER BY MSG_AUDIT_CD DESC LIMIT 1", nativeQuery = true)
    MessageData findFirstByUserEmailTemplateCodeNewest(String emailTo, String templateCode);
}
