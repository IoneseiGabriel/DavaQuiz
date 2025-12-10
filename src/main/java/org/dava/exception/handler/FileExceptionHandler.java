package org.dava.exception.handler;

import org.dava.exception.ExceptionMessage;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Objects;

/**
 * Global exception handler for file-related exceptions.
 */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FileExceptionHandler {

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ExceptionMessage> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex) {

        String message = ex.getAllErrors().stream()
                .map(MessageSourceResolvable::getDefaultMessage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse("Validation failed.");

        return new ResponseEntity<>(new ExceptionMessage(message), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ExceptionMessage> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        return new ResponseEntity<>(new ExceptionMessage(e.getMessage()), HttpStatus.CONTENT_TOO_LARGE);
    }
}