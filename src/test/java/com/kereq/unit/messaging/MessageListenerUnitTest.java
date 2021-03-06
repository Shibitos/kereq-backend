package com.kereq.unit.messaging;

import com.kereq.common.error.RepositoryError;
import com.kereq.helper.AssertHelper;
import com.kereq.messaging.entity.MessageData;
import com.kereq.messaging.error.MessageError;
import com.kereq.messaging.listener.MessageListener;
import com.kereq.messaging.repository.MessageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.env.Environment;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import javax.persistence.EntityManager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

class MessageListenerUnitTest {

    private final MessageRepository messageRepository = Mockito.mock(MessageRepository.class);

    private final JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);

    private final EntityManager entityManager = Mockito.mock(EntityManager.class);

    private MessageListener messageListener;

    @BeforeEach
    public void setup() {
        when(messageRepository.save(Mockito.any(MessageData.class))).thenAnswer(i -> i.getArguments()[0]);
        messageListener = new MessageListener(messageRepository, mailSender, entityManager);
    }

    @Test
    void testSendMessage() {
        MessageData message = new MessageData();
        message.setRetryCount(0);
        when(messageRepository.findForSendingById(1L)).thenReturn(null);
        when(messageRepository.findForSendingById(2L)).thenReturn(message);

        AssertHelper.assertException(RepositoryError.RESOURCE_NOT_FOUND, () -> messageListener.onMessage(1L));

        message.setStatus(MessageData.Status.PENDING);
        messageListener.onMessage(2L);
        assertThat(message.getStatus()).isEqualTo(MessageData.Status.SENT);

        doThrow(new MailSendException("")).when(mailSender).send(Mockito.any(SimpleMailMessage.class));
        message.setStatus(MessageData.Status.PENDING);
        AssertHelper.assertException(MessageError.UNABLE_TO_SEND, () -> messageListener.onMessage(2L));
        assertThat(message.getStatus()).isEqualTo(MessageData.Status.PENDING);
        assertThat(message.getRetryCount()).isEqualTo(1);

        message.setStatus(MessageData.Status.PENDING);
        message.setRetryCount(MessageListener.MAX_RETRY_COUNT - 1);
        AssertHelper.assertException(MessageError.UNABLE_TO_SEND, () -> messageListener.onMessage(2L));
        assertThat(message.getStatus()).isEqualTo(MessageData.Status.FAILED);
        assertThat(message.getRetryCount()).isEqualTo(MessageListener.MAX_RETRY_COUNT);
    }
}
