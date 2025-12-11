package org.dava.mock;

import java.time.LocalDateTime;
import java.util.List;
import org.dava.domain.Game;
import org.dava.domain.Question;
import org.dava.enumeration.GameStatus;
import org.dava.response.GameResponse;

public class GameMockData {
  public static List<Game> getValidGameList() {
    Game game1 =
        Game.builder()
            .id(1L)
            .title("Dragon Quest")
            .description("Epic fantasy adventure game")
            .status(GameStatus.PUBLISHED)
            .createdBy(1L)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Game game2 =
        Game.builder()
            .id(2L)
            .title("Space Runner")
            .description("Sci-fi endless runner game")
            .status(GameStatus.DRAFT)
            .createdBy(1L)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    Game game3 =
        Game.builder()
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

  public static GameResponse getGameResponse(Game game) {
    return GameResponse.builder()
        .id(game.getId())
        .title(game.getTitle())
        .description(game.getDescription())
        .status(game.getStatus())
        .createdBy(game.getCreatedBy())
        .createdAt(String.valueOf(game.getCreatedAt()))
        .updatedAt(String.valueOf(game.getUpdatedAt()))
        .questionCount(game.getQuestionCount() != null ? game.getQuestionCount() : 0)
        .questions(List.of())
        .build();
  }

  public static Game getGameWithNoQuestions() {

    return Game.builder()
        .id(1L)
        .title("Dragon Quest")
        .description("Epic fantasy adventure game")
        .status(GameStatus.PUBLISHED)
        .createdBy(1L)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .questions(List.of())
        .build();
  }

  public static Game getGameWithNullQuestions() {

    return Game.builder()
        .id(1L)
        .title("Dragon Quest")
        .description("Epic fantasy adventure game")
        .status(GameStatus.PUBLISHED)
        .createdBy(1L)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .questions(null)
        .build();
  }

  public static Game getGameWithValidNumberOfQuestions() {

    return Game.builder()
        .id(1L)
        .title("Dragon Quest")
        .description("Epic fantasy adventure game")
        .status(GameStatus.PUBLISHED)
        .createdBy(1L)
        .createdAt(LocalDateTime.now())
        .updatedAt(LocalDateTime.now())
        .questions(List.of(new Question(), new Question()))
        .build();
  }

  public static Game createGame(
      Long id, Long createdBy, String title, String description, GameStatus status) {
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
