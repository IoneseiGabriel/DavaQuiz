package org.dava.mock;

import org.dava.domain.Game;
import org.dava.enumeration.GameStatus;
import org.dava.response.GameResponse;

import java.time.LocalDateTime;
import java.util.List;

public class GameMockData {
    public static List<Game> getValidGameList() {
        Game game1 = Game.builder()
                .id(1L)
                .title("Dragon Quest")
                .description("Epic fantasy adventure game")
                .status(GameStatus.PUBLISHED)
                .createdBy(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Game game2 = Game.builder()
                .id(2L)
                .title("Space Runner")
                .description("Sci-fi endless runner game")
                .status(GameStatus.DRAFT)
                .createdBy(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Game game3 = Game.builder()
                .id(3L)
                .title("Mystery Mansion")
                .description("Solve puzzles in a haunted mansion")
                .status(GameStatus.PUBLISHED)
                .createdBy(1L)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return List.of(game1, game2, game3);
    }

    public static Game getInvalidGame() {

        return Game.builder()
                .id(1L)
                .title(null)
                .description("Epic fantasy adventure game")
                .status(GameStatus.PUBLISHED)
                .createdBy(null)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static GameResponse getGameResponse(Game game) {
        return GameResponse.builder()
                .id(game.getId())
                .title(game.getTitle())
                .description(game.getDescription())
                .status(game.getStatus())
                .createdBy(game.getCreatedBy())
                .createdAt(String.valueOf(game.getCreatedAt()))
                .updatedAt(String.valueOf(game.getUpdatedAt()))
                .build();
    }

    public static Game createGame(Long id,
                                  Long createdBy,
                                  String title,
                                  String description,
                                  GameStatus status) {
        return Game.builder()
                .id(id)
                .createdBy(createdBy)
                .title(title)
                .description(description)
                .status(status)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

}
