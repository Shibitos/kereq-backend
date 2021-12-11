package com.kereq.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ValidationError implements ApplicationError {

    DATE_NOT_PAST(404, "Date is not in the past.");

    private final int httpCode;
    private final String message;
}
