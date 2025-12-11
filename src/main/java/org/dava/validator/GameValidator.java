package org.dava.validator;

import org.dava.domain.Game;
import org.dava.domain.GameStatus;
import org.springframework.stereotype.Component;

@Component
public class GameValidator {


    public void validateGameExists(Game game, Long gameId) {
        if (game == null) {
            throw new IllegalArgumentException("Game not found with id: " + gameId);
        }
    }


    public void validateGameIsDraft(Game game) {
        if (game.getStatus() != GameStatus.DRAFT) {
            throw new IllegalStateException("Cannot add questions to a non-draft game.");
        }
    }


    public void validateUserIsOwner(Game game, Long currentUserId) {
        if (!game.getCreatedBy().equals(currentUserId)) {
            throw new SecurityException("You are not allowed to modify this game.");
        }
    }
}
