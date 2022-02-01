package com.kereq.main.error;

import com.kereq.common.error.ApplicationError;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PhotoError implements ApplicationError {

    NO_FILE(400, "No image file specified."),
    IMAGE_TOO_SMALL(400, "Uploaded image is too small (min. {0}x{0}px)."),
    IMAGE_TOO_BIG(400, "Uploaded image is too big (max. {0}x{1}px).");

    private final int httpCode;
    private final String message;
}
