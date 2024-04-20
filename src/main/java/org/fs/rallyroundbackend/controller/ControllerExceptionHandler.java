package org.fs.rallyroundbackend.controller;

import org.fs.rallyroundbackend.common.ErrorApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Timestamp;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorApi> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode().value())
                .body(buildError(ex.getMessage(), HttpStatus.valueOf(ex.getStatusCode().value())));
    }

    private ErrorApi buildError(String message, HttpStatus httpStatus) {
        return ErrorApi.builder()
                .timestamp(String.valueOf(Timestamp.from(ZonedDateTime.now().toInstant())))
                .error(httpStatus.getReasonPhrase())
                .message(message)
                .build();
    }
}
