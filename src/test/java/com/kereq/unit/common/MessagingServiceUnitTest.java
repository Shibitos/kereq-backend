package com.kereq.unit.common;

import com.kereq.common.constant.ExchangeName;
import com.kereq.common.repository.DictionaryRepository;
import com.kereq.common.service.MessagingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.Mockito.times;

class MessagingServiceUnitTest {

    private final RabbitTemplate rabbitTemplate = Mockito.mock(RabbitTemplate.class);

    private MessagingService messagingService;

    @BeforeEach
    public void setup() {
        messagingService = new MessagingService(rabbitTemplate);
    }

    @Test
    void testSendMessageToQueue() {
        String str = "test";
        messagingService.sendMessageFanout(ExchangeName.MESSAGES, str);
        Mockito.verify(rabbitTemplate, times(1)).convertAndSend(ExchangeName.MESSAGES, "", str);
    }
}
