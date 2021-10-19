package com.kereq.main.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonError implements ApplicationError {

    OTHER_ERROR(500, "Error occurred."),
    VALIDATION_ERROR(400, "Validation failed.");

    private final int httpCode;
    private final String message;
}
