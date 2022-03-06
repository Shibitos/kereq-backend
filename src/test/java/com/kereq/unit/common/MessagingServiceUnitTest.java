package com.kereq.unit.common;

import com.kereq.common.constant.ExchangeName;
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

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private MessagingService messagingService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    void testSendMessageToQueue() {
        String str = "test";
        messagingService.sendMessageFanout(ExchangeName.MESSAGES, str);
        Mockito.verify(rabbitTemplate, times(1)).convertAndSend(ExchangeName.MESSAGES, "", str);
    }
}
