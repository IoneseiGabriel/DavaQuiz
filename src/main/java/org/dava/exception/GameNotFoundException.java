package org.dava.exception;

public class GameNotFoundException extends RuntimeException {

    public GameNotFoundException(Long gameId) {
        super("Game with id " + gameId + " not found");
    }
}
