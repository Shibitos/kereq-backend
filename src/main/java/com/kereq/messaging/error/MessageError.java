package com.kereq.messaging.error;

import com.kereq.common.error.ApplicationError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum MessageError implements ApplicationError {

    UNABLE_TO_SEND(500, "Unable to send message.");

    private final int httpCode;
    private final String message;
}
