package com.kereq.main.exception;

import com.kereq.main.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class ApplicationErrorControllerAdvice {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, final HttpServletRequest request) {
        return handleApplicationException(new ApplicationException(), request);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(Exception e, final HttpServletRequest request) {
        ApplicationException applicationException = (ApplicationException) e;
        HttpStatus status = applicationException.getStatus();

        return new ResponseEntity<>(
                new ErrorResponse(
                        status,
                        applicationException.getMessage(),
                        applicationException.getErrorCode(),
                        request.getRequestURI(),
                        applicationException.getData()
                ),
                status
        );
    }
}
