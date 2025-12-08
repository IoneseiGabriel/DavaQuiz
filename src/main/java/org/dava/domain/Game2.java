package org.dava.domain;

import java.util.List;

public class Game2 {

    private List<Game> games;

    public List<Game> getGames() {

        if(games.size()==1)
            return List.of(games.get(0));
        return games;
    }
}
