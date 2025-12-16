package org.dava.exception;

public class ExistentFileException extends RuntimeException {
  public ExistentFileException(String message) {
    super(message);
  }
}
