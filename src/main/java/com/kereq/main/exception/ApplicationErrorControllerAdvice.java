package com.kereq.main.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kereq.main.dto.ErrorResponse;
import com.kereq.main.error.CommonError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@ControllerAdvice
public class ApplicationErrorControllerAdvice {

    @Autowired
    private ObjectMapper mapper;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception, final HttpServletRequest request) {
        return handleApplicationException(new ApplicationException(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception,
                                                                   final HttpServletRequest request) {
        Map<String, List<String>> data = new HashMap<>();
        exception.getFieldErrors().forEach(fieldError -> {
            String field = fieldError.getField();
            if (data.containsKey(field)) {
                data.get(field).add(fieldError.getDefaultMessage());
            } else {
                data.put(field, new ArrayList<>(Collections.singleton(fieldError.getDefaultMessage())));
            }
        });
        ArrayNode mainJSON = mapper.createArrayNode();
        data.forEach((field, messages) -> {
            ObjectNode entry = mapper.createObjectNode();
            entry.put("field", field);
            ArrayNode array = entry.putArray("messages");
            messages.forEach(array::add);
            mainJSON.add(entry);
        });

        return handleApplicationException(new ApplicationException(mainJSON,
                CommonError.VALIDATION_ERROR),
                request);
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException exception,
                                                                    final HttpServletRequest request) {
        HttpStatus status = exception.getStatus();
        //request.get

        return new ResponseEntity<>(
                new ErrorResponse(
                        status,
                        exception.getMessage(),
                        exception.getErrorCode(),
                        request.getRequestURI(),
                        exception.getData()
                ),
                status
        );
    }
}
