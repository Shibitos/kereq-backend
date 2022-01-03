package com.kereq.unit;

import com.kereq.common.error.CommonError;
import com.kereq.helper.AssertHelper;
import com.kereq.messaging.entity.MessageData;
import com.kereq.messaging.entity.MessageTemplateData;
import com.kereq.messaging.repository.MessageRepository;
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

class EmailServiceUnitTest {

    @Mock
    private MessageRepository messageRepository;

    @Mock
    private Environment env;

    @InjectMocks
    private EmailService emailService;

    private final String[] validEmails = {
            "email@example.com",
            "firstname.lastname@example.com",
            "email@subdomain.example.com",
            "firstname+lastname@example.com",
            "email@[123.123.123.123]",
            "\"email\"@example.com",
            "1234567890@example.com",
            "email@example-one.com",
            "_______@example.com",
            "email@example.name",
            "email@example.museum",
            "email@example.co.jp"
    };

    private final String[] invalidEmails = {
            "plainaddress",
            "#@%^%#$@#$@#.com",
            "@example.com",
            "email.example.com",
            "email@example@example.com",
            ".email@example.com",
            "email.@example.com",
            "email..email@example.com",
            "email@-example.com",
            "email@111.222.333.44444",
            "email@example..com",
            "Abc..123@example.com",
            "”(),:;<>[\\]@example.com",
            "this\\ is\"really\"not\\allowed@example.com"
    };

    @BeforeEach
    public void setup() { //TODO: sendMessageTest: if fail, retry count up (on limit = failed), else status sent
        MockitoAnnotations.initMocks(this);
        when(env.getProperty("email.support")).thenReturn("kereq@ethereal.email");
        when(messageRepository.save(Mockito.any(MessageData.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    @Test
    void testCreateMessage() {
        MessageTemplateData template = getTestTemplate("test_noparam_1", "Test subject", "Test body");
        MessageData message = emailService.createMessageFromTemplate(template, validEmails[0], null);
        assertThat(message.getRetryCount()).isZero();
        assertThat(message.getStatus()).isEqualTo(MessageData.Status.PENDING);
    }

    @Test
    void testEmailValidation() {
        MessageTemplateData template = getTestTemplate("t", "S", "B");
        for (String validEmail : validEmails) {
            emailService.createMessageFromTemplate(template, validEmail, null);
        }
        for (String invalidEmail : invalidEmails) {
            AssertHelper.assertException(CommonError.INVALID_ERROR,
                    () -> emailService.createMessageFromTemplate(template, invalidEmail, null), invalidEmail);
        }
    }

    @Test
    void testParseMessageComplete() {
        Map<String, String> params = new HashMap<>() {
            {
                put("param1", "test1");
                put("param2", "test2");
                put("param3", "test3");
            }
        };
        MessageTemplateData template = getTestTemplate("test_param_1",
                "Test {{param1}} subject {{param2}}", "{{param1}}Test {{param2}} body{{param3}}");
        MessageData message = emailService.createMessageFromTemplate(template, validEmails[0], params);
        assertThat(message.getSubject()).isEqualTo("Test test1 subject test2");
        assertThat(message.getBody()).isEqualTo("test1Test test2 bodytest3");
    }

    @Test
    void testParseMessageIncomplete() {
        Map<String, String> params = new HashMap<>() {
            {
                put("param1", "test1");
            }
        };
        MessageTemplateData template = getTestTemplate("test_param_2",
                "Test {{param1}} subject", "{{param1}}Test {{param2}}");
        AssertHelper.assertException(CommonError.MISSING_ERROR,
                () -> emailService.createMessageFromTemplate(template, validEmails[0], params));
        AssertHelper.assertException(CommonError.MISSING_ERROR,
                () -> emailService.createMessageFromTemplate(template, validEmails[0], null));
    }

    private MessageTemplateData getTestTemplate(String code, String subject, String body) {
        MessageTemplateData template = new MessageTemplateData();
        template.setCode(code);
        template.setSubject(subject);
        template.setBody(body);
        return template;
    }
}
