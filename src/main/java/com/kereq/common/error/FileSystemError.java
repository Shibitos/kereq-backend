package com.kereq.common.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum FileSystemError implements ApplicationError {

    NO_ACCESS_CREATE_DIR(500, "Error occurred.");

    private final int httpCode;
    private final String message;
}
