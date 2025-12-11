package org.dava.exception;

public class InvalidJwtException extends RuntimeException {

  public InvalidJwtException() {
    super("Invalid or expired JWT token");
  }
}
