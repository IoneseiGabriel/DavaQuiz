package org.dava.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidGameException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidGame(InvalidGameException ex) {
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "error", "Invalid game data",
                "message", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        // You might want to log ex here
        Map<String, Object> body = Map.of(
                "timestamp", Instant.now().toString(),
                "error", "Internal server error",
                "message", ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }
}