package org.dava.exception.handler;

import java.util.NoSuchElementException;
import lombok.NonNull;
import org.dava.exception.ExceptionMessage;
import org.dava.exception.InvalidGameException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<@NonNull ExceptionMessage> handleIllegalArgumentException(
      IllegalArgumentException e) {
    return ResponseEntity.badRequest()
        .body(ExceptionMessage.of("An error occurred.", e.getMessage()));
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<@NonNull ExceptionMessage> handleNoSuchElementException(
      NoSuchElementException e) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(ExceptionMessage.of("An error occurred", e.getMessage()));
  }

  @ExceptionHandler(InvalidGameException.class)
  public ResponseEntity<@NonNull ExceptionMessage> handleInvalidGame(InvalidGameException e) {
    return ResponseEntity.badRequest()
        .body(ExceptionMessage.of("Invalid game data.", e.getMessage()));
  }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ExceptionMessage> handleNoSuchElementException(NoSuchElementException e) {
        return new ResponseEntity<>(new ExceptionMessage(e.getMessage()), HttpStatus.NOT_FOUND);
    }

    return ResponseEntity.internalServerError()
        .body(ExceptionMessage.of("An error occurred.", e.getMessage()));
  }
}
