package org.dava.exception.handler;

import static org.junit.jupiter.api.Assertions.*;

import java.util.NoSuchElementException;
import org.dava.exception.ExceptionMessage;
import org.dava.exception.InvalidGameException;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  @Test
  void handleIllegalArgumentExceptionReturnsBadRequestAndBody() {

    IllegalArgumentException e = new IllegalArgumentException("Invalid input");

    ResponseEntity<@NotNull ExceptionMessage> response = handler.handleIllegalArgumentException(e);

    assertEquals(
        HttpStatus.BAD_REQUEST,
        response.getStatusCode(),
        "IllegalArgumentException should map to HTTP 400");
    assertNotNull(response.getBody(), "Response body should not be null");
  }

  @Test
  void handleNoSuchElementExceptionReturnsNotFoundAndBody() {

    NoSuchElementException e = new NoSuchElementException("Game not found");

    ResponseEntity<@NotNull ExceptionMessage> response = handler.handleNoSuchElementException(e);

    assertEquals(
        HttpStatus.NOT_FOUND,
        response.getStatusCode(),
        "NoSuchElementException should map to HTTP 404");
    assertNotNull(response.getBody(), "Response body should not be null");
  }

  @Test
  void handleInvalidGameReturnsBadRequestWithExceptionMessageBody() {

    InvalidGameException e = new InvalidGameException("Invalid game error");

    ResponseEntity<@NotNull ExceptionMessage> response = handler.handleInvalidGame(e);

    assertEquals(
        HttpStatus.BAD_REQUEST,
        response.getStatusCode(),
        "InvalidGameException should map to HTTP 400");
    assertNotNull(response.getBody(), "Response body should not be null");
  }

  @Test
  void handleGenericReturnsInternalServerErrorWithExceptionMessageBody() {

    Exception e = new RuntimeException("Something went wrong");

    ResponseEntity<@NotNull ExceptionMessage> response = handler.handleGeneric(e);

    assertEquals(
        HttpStatus.INTERNAL_SERVER_ERROR,
        response.getStatusCode(),
        "Generic Exception should map to HTTP 500");
    assertNotNull(response.getBody(), "Response body should not be null");
  }
}
