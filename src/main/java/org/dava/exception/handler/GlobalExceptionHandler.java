package org.dava.exception.handler;

import java.util.NoSuchElementException;
import org.dava.exception.ExceptionMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ExceptionMessage> handleIllegalArgumentException(
      IllegalArgumentException e) {
    return new ResponseEntity<>(new ExceptionMessage(e.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<ExceptionMessage> handleException(NoSuchElementException e) {
    return new ResponseEntity<>(new ExceptionMessage(e.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ExceptionMessage> handleException(Exception e) {
    return new ResponseEntity<>(
        new ExceptionMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
