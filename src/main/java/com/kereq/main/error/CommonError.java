package com.kereq.main.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonError implements ApplicationError {

    OTHER_ERROR(500, "Error occurred."),
    VALIDATION_ERROR(400, "Validation failed."),
    INVALID_ERROR(400, "Invalid {0}."),
    MISSING_ERROR(404, "Missing {0}."),
    TEST_ERROR(500, "Test {0} erro{1}r");

    private final int httpCode;
    private final String message;
}
