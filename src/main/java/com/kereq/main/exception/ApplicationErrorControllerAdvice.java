package com.kereq.main.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.kereq.common.error.CommonError;
import com.kereq.main.dto.ErrorResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@Slf4j
@ControllerAdvice
public class ApplicationErrorControllerAdvice {

    @Autowired
    private ObjectMapper mapper;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleException(Exception exception, final HttpServletRequest request) {
        log.error("Exception occurred", exception);
        return handleApplicationException(new ApplicationException(), request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidationException(MethodArgumentNotValidException exception,
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

        CommonError error = CommonError.VALIDATION_ERROR;
        HttpStatus status = HttpStatus.valueOf(error.getHttpCode());

        return new ResponseEntity<>(
                new ErrorResponseDTO(
                        status,
                        error.buildMessage(),
                        error.name(),
                        request.getRequestURI(),
                        mainJSON
                ),
                status
        );
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponseDTO> handleApplicationException(ApplicationException exception,
                                                                       final HttpServletRequest request) {
        HttpStatus status = exception.getStatus();
        if (!CommonError.OTHER_ERROR.getMessage().equals(exception.getErrorCode())) {
            log.error(exception.getMessage(), exception);
        }

        return new ResponseEntity<>(
                new ErrorResponseDTO(
                        status,
                        exception.getMessage(),
                        exception.getErrorCode(),
                        request.getRequestURI()
                ),
                status
        );
    }
}
