package com.examen.hei.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        // Si le message contient "Cannot modify", c'est un 403
        HttpStatus status = ex.getMessage() != null && ex.getMessage().contains("Cannot modify")
                ? HttpStatus.FORBIDDEN
                : HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(status)
                .body(Map.of(
                        "timestamp", Instant.now(),
                        "status", status.value(),
                        "error", status.getReasonPhrase(),
                        "message", ex.getMessage()
                ));
    }

    // NOUVEAU : Gestion spécifique pour IllegalStateException (403)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "timestamp", Instant.now(),
                        "status", 403,
                        "error", "Forbidden",
                        "message", ex.getMessage()
                ));
    }
}