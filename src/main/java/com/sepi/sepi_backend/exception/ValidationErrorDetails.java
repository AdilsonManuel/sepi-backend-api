package com.sepi.sepi_backend.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class ValidationErrorDetails {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;

    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, List<String>> validationErrors;

    public ValidationErrorDetails() {
        this.timestamp = LocalDateTime.now();
    }

    public ValidationErrorDetails(int status, String error, String message, String path, Map<String, List<String>> validationErrors) {
        this();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.validationErrors = validationErrors;
    }
}

