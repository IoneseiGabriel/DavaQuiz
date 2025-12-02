package org.dava.exception;

public class InvalidGameException extends RuntimeException{
    public InvalidGameException(String message) {
        super(message);
    }
}
