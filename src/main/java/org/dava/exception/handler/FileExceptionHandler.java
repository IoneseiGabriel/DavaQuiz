package org.dava.exception.handler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Objects;
import org.dava.exception.ExceptionMessage;
import org.dava.exception.ExistentFileException;
import org.dava.exception.InvalidFileException;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/** Global exception handler for file-related exceptions. */
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FileExceptionHandler {

  @ExceptionHandler(HandlerMethodValidationException.class)
  public ResponseEntity<ExceptionMessage> handleHandlerMethodValidationException(
      HandlerMethodValidationException ex) {

    String message =
        ex.getAllErrors().stream()
            .map(MessageSourceResolvable::getDefaultMessage)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse("Validation failed.");

    return new ResponseEntity<>(
        ExceptionMessage.of("Validation Exception", message), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<ExceptionMessage> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException e) {
    return new ResponseEntity<>(
        ExceptionMessage.of("Size Exception", e.getMessage()), HttpStatus.CONTENT_TOO_LARGE);
  }

  @ExceptionHandler(FileNotFoundException.class)
  public ResponseEntity<ExceptionMessage> handleFileNotFoundException(FileNotFoundException e) {
    return new ResponseEntity<>(
        ExceptionMessage.of("File Not Found Exception", e.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ExistentFileException.class)
  public ResponseEntity<ExceptionMessage> handleExistentFileException(ExistentFileException e) {
    return new ResponseEntity<>(
        ExceptionMessage.of("Existent File Exception", e.getMessage()), HttpStatus.CONFLICT);
  }

  @ExceptionHandler({IOException.class, InvalidFileException.class})
  public ResponseEntity<ExceptionMessage> handleIOException(Exception e) {
    return new ResponseEntity<>(
        ExceptionMessage.of("File Operation Failed Exception", e.getMessage()),
        HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
