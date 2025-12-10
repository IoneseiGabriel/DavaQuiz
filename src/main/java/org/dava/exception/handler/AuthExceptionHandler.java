package org.dava.exception.handler;

import org.dava.exception.ExceptionMessage;
import org.dava.exception.InvalidJwtException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for authentication and authorization-related exceptions.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthExceptionHandler {
    @ExceptionHandler(InvalidJwtException.class)
    public ResponseEntity<ExceptionMessage> handleInvalidJwtException(InvalidJwtException e) {
        return new ResponseEntity<>(new ExceptionMessage(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }
}
