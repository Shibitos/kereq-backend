package com.kereq.main.error;

import java.text.MessageFormat;

public interface ApplicationError {

    String name();

    int getHttpCode();

    String getMessage();

    default String buildMessage(Object... msgParams) {
        String format = getMessage();
        return MessageFormat.format(format, msgParams);
    }
}
