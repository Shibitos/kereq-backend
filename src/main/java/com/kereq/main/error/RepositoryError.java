package com.kereq.main.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RepositoryError implements ApplicationError {

    RESOURCE_NOT_FOUND(404, "Resource with ID {0} could not be found."),
    RESOURCE_NOT_FOUND_VALUE(404, "Resource with ''{0}'' could not be found."),
    RESOURCE_ALREADY_EXISTS(409, "Resource containing value ''{0}'' in field ''{1}'' already exists.");

    private final int httpCode;
    private final String message;
}
