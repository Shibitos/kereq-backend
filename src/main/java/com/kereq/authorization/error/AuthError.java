package com.kereq.authorization.error;

import com.kereq.main.error.ApplicationError;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthError implements ApplicationError {

    TOKEN_INVALID(404, "Invalid token."),
    TOKEN_EXPIRED(410, "Token expired."),
    TOKEN_TOO_EARLY(400, "It is too early to resend token.");

    private final int httpCode;
    private final String message;
}
