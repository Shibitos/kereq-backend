package com.kereq.main.exception;

import com.kereq.main.error.ApplicationError;
import com.kereq.main.error.CommonError;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
@Setter
public class ApplicationException extends RuntimeException {

    private static final long serialVersionUID = -2234686647803914126L;

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
