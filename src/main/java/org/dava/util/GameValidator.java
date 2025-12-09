package org.dava.util;

import org.dava.domain.Game;
import org.dava.enumeration.GameStatus;
import org.dava.exception.InvalidGameException;
import org.springframework.stereotype.Component;

@Component
public class GameValidator {

    public void handleStatusTransition(Game game, GameStatus requestedStatus){
        GameStatus currentStatus = game.getStatus();

        if(currentStatus == requestedStatus){
            return;
        }

        if (currentStatus == GameStatus.DRAFT && requestedStatus == GameStatus.PUBLISHED){
            return;
        }

        throw new InvalidGameException("Invalid status transition from "+ currentStatus + " to "+ requestedStatus);
    }
}
