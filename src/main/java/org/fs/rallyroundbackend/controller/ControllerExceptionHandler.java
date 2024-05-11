package org.fs.rallyroundbackend.controller;

import com.mercadopago.exceptions.MPException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.fs.rallyroundbackend.common.ErrorApi;
import org.fs.rallyroundbackend.exception.common.RallyRoundApiException;
import org.fs.rallyroundbackend.exception.event.InconsistentEventException;
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
    public ResponseEntity<ErrorApi> handleException(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode().value())
                .body(buildError(ex.getMessage(), HttpStatus.valueOf(ex.getStatusCode().value())));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorApi> handleException(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(this.buildError(ex.getMessage(),
                HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorApi> handleException(EntityExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.buildError(ex.getMessage(),
                HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(MPException.class)
    public ResponseEntity<ErrorApi> handleException(MPException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.buildError(
                "Error during the interaction with the mercado pago API.",
                HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }

    @ExceptionHandler(InconsistentEventException.class)
    public ResponseEntity<ErrorApi> handleException(InconsistentEventException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(this.buildError(
                ex.getMessage(),
                HttpStatus.INTERNAL_SERVER_ERROR)
        );
    }

    @ExceptionHandler(RallyRoundApiException.class)
    public ResponseEntity<ErrorApi> handleException(RallyRoundApiException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(this.buildError(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST)
        );
    }

    private ErrorApi buildError(String message, HttpStatus httpStatus) {
        return ErrorApi.builder()
                .timestamp(String.valueOf(Timestamp.from(ZonedDateTime.now().toInstant())))
                .error(httpStatus.getReasonPhrase())
                .message(message)
                .build();
    }
}
