package com.kereq.main.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@AllArgsConstructor
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ErrorResponseDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private Date timestamp;

    private int code;

    private String status;

    private String error;

    private String message;

    private String path;

    private Object data;

    public ErrorResponseDTO() {
        timestamp = new Date();
    }

    public ErrorResponseDTO(HttpStatus httpStatus, String message, String error, String path) {
        this();

        this.code = httpStatus.value();
        this.status = httpStatus.name();
        this.message = message;
        this.path = path;
        this.error = error;
    }

    public ErrorResponseDTO(HttpStatus httpStatus, String message, String error, String path, Object data) {
        this(httpStatus, message, error, path);
        this.data = data;
    }
}
