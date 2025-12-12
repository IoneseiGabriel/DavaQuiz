package org.dava.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.dava.mock.GameMockData.getGameResponse;
import static org.dava.mock.GameMockData.getValidGameList;
import static org.dava.mock.PageMockData.getPageResponse;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import org.dava.domain.Game;
import org.dava.domain.Question;
import org.dava.mock.GameMockData;
import org.dava.response.GameResponse;
import org.dava.response.PageResponse;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
class GameMapperTest {
  private final GameMapper gameMapper = Mappers.getMapper(GameMapper.class);

  private List<Game> validGameList;

  @BeforeEach
  void setUp() {
    validGameList = getValidGameList();
  }

  @Test
  void toGameResponseWithValidGameReturnsGameResponse() {
    // Arrange
    GameResponse expected = getGameResponse(validGameList.get(0));

    // Act
    GameResponse actual = gameMapper.toGameResponse(validGameList.get(0));

    // Assert
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(expected);
  }

  @Test
  void toGameResponseListWithValidGameListReturnsGameResponseList() {
    // Arrange
    List<GameResponse> expected =
        validGameList.stream().map(GameMockData::getGameResponse).toList();

    // Act
    List<GameResponse> actual = gameMapper.toGameResponseList(validGameList);

    // Assert
    assertThat(actual)
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(expected);
  }

  @Test
  void toGameResponseWithNullGameReturnsNull() {
    // Act
    GameResponse actual = gameMapper.toGameResponse(null);

    // Assert
    Assertions.assertNull(actual);
  }

  @Test
  void toGameResponseListWithNullListReturnsNull() {
    // Act
    List<GameResponse> actual = gameMapper.toGameResponseList(null);

    // Assert
    Assertions.assertNull(actual);
  }

  @Test
  void toGameResponseWithNullCreatedAtAndNullUpdatedAtReturnsGameResponse() {
    // Arrange
    Game game = validGameList.get(0);
    game.setCreatedAt(null);
    game.setUpdatedAt(null);

    // Act
    GameResponse actual = gameMapper.toGameResponse(validGameList.get(0));

    // Assert
    assertThat(actual)
        .usingRecursiveComparison()
        .comparingOnlyFields("createdAt", "updatedAt")
        .isEqualTo(game);
  }

  @Test
  void toResponsePageWithValidPageAndFiltersReturnsGamePageResponse() {
    // Arrange
    Page<@NotNull Game> gamePage = new PageImpl<>(validGameList);

    List<GameResponse> gameResponseList =
        validGameList.stream().map(GameMockData::getGameResponse).toList();
    Page<@NotNull GameResponse> gameResponsePage = new PageImpl<>(gameResponseList);

    PageResponse<GameResponse> expected = getPageResponse(gameResponsePage, new HashSet<>());

    // Act
    PageResponse<GameResponse> actual = gameMapper.toResponsePage(gamePage, new HashSet<>());

    // Assert
    assertThat(actual.getContent())
        .usingRecursiveComparison()
        .ignoringFields("createdAt", "updatedAt")
        .isEqualTo(expected.getContent());
  }

  @Test
  void toGameResponseMapsBasicFields() {
    Game game = GameMockData.getGameWithValidNumberOfQuestions();

    GameResponse response = gameMapper.toGameResponse(game);

    assertNotNull(response, "Mapped response should not be null");
    assertEquals(game.getId(), response.getId(), "Mapped game id should match");
    assertEquals(game.getTitle(), response.getTitle(), "Mapped game title should match");
    assertEquals(game.getDescription(), response.getDescription(), "");
    assertEquals(game.getStatus(), response.getStatus(), "Mapped game status should match");
    assertEquals(
        game.getCreatedBy(), response.getCreatedBy(), "Mapped game created by should match");
  }

  @Test
  void toGameWithNullEntity() {

    GameResponse response = gameMapper.toGameResponse(null);

    assertNull(response, "Mapped response should be null if game is invalid");
  }

  @Test
  void toGameResponseComputesQuestionCountWhenQuestionsNotNull() {
    Game game =
        Game.builder()
            .id(1L)
            .title("Test Game")
            .questions(List.of(new Question(), new Question(), new Question()))
            .build();

    GameResponse response = gameMapper.toGameResponse(game);

    assertNotNull(response);
    assertEquals(
        3, response.getQuestionCount(), "questionCount should equal size of questions list");
  }

  @Test
  void toGameResponseQuestionCountIsZeroWhenQuestionsNull() {
    Game game = GameMockData.getGameWithNullQuestions();

    GameResponse response = gameMapper.toGameResponse(game);

    assertNotNull(response);
    assertEquals(
        0, response.getQuestionCount(), "questionCount should be 0 when questions list is null");
  }

  @Test
  void toGameResponseFormatsCreatedAtAndUpdatedAtToString() {
    LocalDateTime createdAt = LocalDateTime.of(2024, 3, 10, 15, 45, 30);
    LocalDateTime updatedAt = LocalDateTime.of(2024, 3, 11, 16, 0, 0);

    Game game =
        Game.builder().id(1L).title("Test Game").createdAt(createdAt).updatedAt(updatedAt).build();

    GameResponse response = gameMapper.toGameResponse(game);

    assertNotNull(response);
    assertEquals(
        createdAt.toString(),
        response.getCreatedAt(),
        "createdAt should be mapped using LocalDateTime.toString()");
    assertEquals(
        updatedAt.toString(),
        response.getUpdatedAt(),
        "updatedAt should be mapped using LocalDateTime.toString()");
  }

  @Test
  void toGameResponseSetsCreatedAtAndUpdatedAtNullWhenSourceNull() {
    Game game = Game.builder().id(1L).title("Test Game").createdAt(null).updatedAt(null).build();

    GameResponse response = gameMapper.toGameResponse(game);

    assertNotNull(response);
    assertNull(response.getCreatedAt(), "createdAt should be null when source createdAt is null");
    assertNull(response.getUpdatedAt(), "updatedAt should be null when source updatedAt is null");
  }
}
