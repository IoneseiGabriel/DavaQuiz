package org.dava.exception;

public class TooManyLoginAttemptsException extends RuntimeException {

  public TooManyLoginAttemptsException(long remainingSeconds) {
    super("Too many login attempts. Retry in " + remainingSeconds + " seconds.");
  }
}
