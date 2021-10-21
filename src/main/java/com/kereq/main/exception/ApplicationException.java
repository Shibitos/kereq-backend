package com.kereq.main.exception;

import com.kereq.main.error.ApplicationError;
import com.kereq.main.error.CommonError;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class ApplicationException extends RuntimeException {

    private String errorCode;

    private HttpStatus status;

    private Object data;

    public ApplicationException() {
        this(CommonError.OTHER_ERROR);
    }

    public ApplicationException(ApplicationError error, Object... msgParams) {
        super(error.buildMessage(msgParams));
        errorCode = error.name();
        status = HttpStatus.valueOf(error.getHttpCode());
    }

    public ApplicationException(Object data, ApplicationError error, Object... msgParams) {
        this(error, msgParams);
        this.data = data;
    }
}
