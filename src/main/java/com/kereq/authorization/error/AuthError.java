package com.kereq.authorization.error;

import com.kereq.main.error.ApplicationError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthError implements ApplicationError {

    TOKEN_INVALID(404, "Invalid token."),
    TOKEN_EXPIRED(410, "Token expired."),
    TOKEN_TOO_EARLY(400, "It is too early to resend token."),
    USER_ALREADY_ACTIVATED(400, "User already activated."),
    NO_ACCESS(403, "You have no access to this resource");

    private final int httpCode;
    private final String message;
}
