package com.kereq.unit;

import com.kereq.messaging.entity.MessageData;
import com.kereq.messaging.entity.MessageTemplateData;
import com.kereq.messaging.repository.MessageRepository;
import com.kereq.messaging.repository.MessageTemplateRepository;
import com.kereq.messaging.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.core.env.Environment;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

public class EmailServiceUnitTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private Environment env;

    @InjectMocks
    private EmailService emailService;

    private final String[] validEmails = { "test1@test1.com" };

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        when(env.getProperty("email.support")).thenReturn("kereq@ethereal.email");
        when(messageRepository.save(Mockito.any(MessageData.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    public void testCreateMessage() {
        MessageTemplateData template = getTestTemplate("test_noparam_1", "Test subject", "Test body");
        MessageData message = emailService.createMessageFromTemplate(template, validEmails[0], null); //TODO: invalid mails
        assertThat(message.getRetryCount()).isEqualTo(0);
        assertThat(message.getStatus()).isEqualTo(MessageData.Status.PENDING);
    }

    @Test
    public void testParseMessageComplete() {
        Map<String, String> paramsComplete = new HashMap<>() {
            {
                put("param1", "test1");
                put("param2", "test2");
                put("param3", "test3");
            }
        };
        MessageTemplateData template = getTestTemplate("test_param_1", "Test {{param1}} subject {{param2}}", "{{param1}}Test {{param2}} body{{param3}}");
        MessageData message = emailService.createMessageFromTemplate(template, validEmails[0], paramsComplete);
        assertThat(message.getSubject()).isEqualTo("Test test1 subject test2");
        assertThat(message.getBody()).isEqualTo("test1Test test2 bodytest3");
    }

    private MessageTemplateData getTestTemplate(String code, String subject, String body) {
        MessageTemplateData template = new MessageTemplateData();
        template.setCode(code);
        template.setSubject(subject);
        template.setBody(body);
        return template;
    }
}
