package com.kereq.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileSystemError implements ApplicationError {

    RESOURCE_NOT_FOUND(404, "Resource not found.");

    private final int httpCode;
    private final String message;
}
