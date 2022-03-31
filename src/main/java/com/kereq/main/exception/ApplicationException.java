package com.kereq.main.exception;

import com.kereq.common.error.ApplicationError;
import com.kereq.common.error.CommonError;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApplicationException extends RuntimeException {

    static final long serialVersionUID = -2234686647803914126L;

    private final String errorCode;

    private final HttpStatus status;

    public ApplicationException() {
        this(CommonError.OTHER_ERROR);
    }

    public ApplicationException(ApplicationError error, Object... msgParams) {
        super(error.buildMessage(msgParams));
        errorCode = error.name();
        status = HttpStatus.valueOf(error.getHttpCode());
    }
}
