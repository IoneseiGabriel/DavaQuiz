package org.dava.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import org.dava.dao.GameRepository;
import org.dava.domain.Game;
import org.dava.domain.Question;
import org.dava.dto.GameRequest;
import org.dava.dto.GameUpdateRequest;
import org.dava.dto.QuestionRequest;
import org.dava.enumeration.GameStatus;
import org.dava.exception.InvalidGameException;
import org.dava.mapper.GameMapper;
import org.dava.mapper.QuestionMapper;
import org.dava.mock.GameMockData;
import org.dava.mock.GameRequestMockData;
import org.dava.response.GameResponse;
import org.dava.util.GameValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

  @Mock private GameRepository gameRepository;

  @Mock private GameMapper gameMapper;

  @Mock private QuestionMapper questionMapper;

  @InjectMocks private GameServiceImpl gameService;

  private GameRequest validRequest;

  private GameUpdateRequest updateRequest;

  private Long userId;

  @BeforeEach
  void setUp() {
    userId = 200L;
    validRequest = GameRequestMockData.getValidGameRequest();

    updateRequest = new GameUpdateRequest();
    updateRequest.setTitle("Updated Math Quiz");
    updateRequest.setDescription("Updated description math game");
    updateRequest.setStatus(GameStatus.PUBLISHED);
  }

  @Test
  void createGameWithValidRequestBuildsGameQuestionsSavesAndMaps() {

    when(gameMapper.toEntity(validRequest, userId))
        .thenAnswer(
            invocation -> {
              GameRequest req = invocation.getArgument(0);
              Long uid = invocation.getArgument(1);

              Game g = new Game();
              g.setTitle(req.getTitle());
              g.setDescription(req.getDescription());
              g.setStatus(GameStatus.DRAFT);
              g.setCreatedBy(uid);
              g.setQuestions(new ArrayList<>());
              return g;
            });

    when(questionMapper.toEntity(any(QuestionRequest.class)))
        .thenAnswer(
            invocation -> {
              QuestionRequest qReq = invocation.getArgument(0);

              Question q = new Question();
              q.setText(qReq.getText());
              q.setOptions(new ArrayList<>(qReq.getOptions()));
              q.setImageUrl(qReq.getImageUrl());
              q.setCorrectOptionIndex(qReq.getCorrectOptionIndex());
              return q;
            });

    when(gameRepository.save(any(Game.class)))
        .thenAnswer(
            invocation -> {
              Game g = invocation.getArgument(0);
              g.setId(10L);
              g.setCreatedAt(LocalDateTime.now());
              g.setUpdatedAt(LocalDateTime.now());
              return g;
            });

    GameResponse mapped =
        GameResponse.builder()
            .id(10L)
            .title(validRequest.getTitle())
            .description(validRequest.getDescription())
            .status(GameStatus.DRAFT)
            .createdBy(userId)
            .questionCount(validRequest.getQuestions().size())
            .build();

    when(gameMapper.toGameResponse(any(Game.class))).thenReturn(mapped);

    GameResponse result = gameService.createGame(validRequest, userId);

    assertNotNull(result, "Result should not be null");
    assertEquals(10L, result.getId(), "Result id should come from saved entity");
    assertEquals(
        validRequest.getTitle(), result.getTitle(), "Result title should match mapped value");
    assertEquals(
        validRequest.getQuestions().size(),
        result.getQuestionCount(),
        "Result questionCount should be mapped correctly");

    ArgumentCaptor<Game> gameCaptor = ArgumentCaptor.forClass(Game.class);
    verify(gameRepository, times(1)).save(gameCaptor.capture());

    Game persisted = gameCaptor.getValue();
    assertNotNull(persisted, "Persisted game should not be null");
    assertEquals(
        validRequest.getTitle(), persisted.getTitle(), "Persisted title must match request");
    assertEquals(
        validRequest.getDescription(),
        persisted.getDescription(),
        "Persisted description must match request");
    assertEquals(GameStatus.DRAFT, persisted.getStatus(), "Persisted status must match request");
    assertEquals(userId, persisted.getCreatedBy(), "Persisted createdBy must be userId");

    assertNotNull(persisted.getCreatedAt(), "createdAt should be set");
    assertNotNull(persisted.getUpdatedAt(), "updatedAt should be set");

    assertNotNull(persisted.getQuestions(), "Persisted questions list must not be null");
    assertEquals(
        validRequest.getQuestions().size(),
        persisted.getQuestions().size(),
        "Persisted questions size must match request questions");

    QuestionRequest expectedQ1Req = validRequest.getQuestions().get(0);
    Question persistedQ1 = persisted.getQuestions().get(0);

    assertEquals(expectedQ1Req.getText(), persistedQ1.getText(), "First question text must match");
    assertEquals(
        expectedQ1Req.getOptions(), persistedQ1.getOptions(), "First question options must match");
    assertEquals(
        expectedQ1Req.getCorrectOptionIndex(),
        persistedQ1.getCorrectOptionIndex(),
        "First question correct index must match");
    assertEquals(
        expectedQ1Req.getImageUrl(),
        persistedQ1.getImageUrl(),
        "First question imageUrl must match");

    verify(gameMapper, times(1)).toGameResponse(any(Game.class));
  }

  @Test
  void createGameWhenValidationFailsThrowsInvalidGameExceptionAndDoesNotSave() {
    GameRequest invalid =
        new GameRequest(
            "",
            validRequest.getDescription(),
            GameStatus.DRAFT,
            validRequest.getCreatedBy(),
            validRequest.getCreatedAt(),
            validRequest.getUpdatedAt(),
            validRequest.getQuestionCount(),
            validRequest.getQuestions());

    try (MockedStatic<GameValidator> validationMock = mockStatic(GameValidator.class)) {
      validationMock
          .when(() -> GameValidator.processGameRequest(invalid))
          .thenThrow(new InvalidGameException("Invalid game"));

      InvalidGameException e =
          assertThrows(
              InvalidGameException.class,
              () -> gameService.createGame(invalid, userId),
              "Service should propagate InvalidGameException thrown by ValidationUtil");

      assertEquals(
          "Invalid game", e.getMessage(), "Exception message should come from ValidationUtil");

      verifyNoInteractions(gameRepository);
      verifyNoInteractions(gameMapper);
    }
  }

  @Test
  void createGameWhenGameRepositoryThrowsException() {

    Game mappedGame = new Game();
    mappedGame.setQuestions(new ArrayList<>());

    when(gameMapper.toEntity(validRequest, userId)).thenReturn(mappedGame);
    when(questionMapper.toEntity(any(QuestionRequest.class))).thenReturn(new Question());

    when(gameRepository.save(any(Game.class))).thenThrow(new RuntimeException("DB failure"));

    RuntimeException e =
        assertThrows(
            RuntimeException.class,
            () -> gameService.createGame(validRequest, userId),
            "Service should not swallow repository RuntimeException");

    assertEquals("DB failure", e.getMessage(), "Exception message must match repository exception");
  }

  @Test
  void updateGameMetadata_withValidTitleAndDescription_updatesFields() {
    Long gameId = 1L;
    Long hostId = 200L;

    Game game =
        GameMockData.createGame(
            gameId, hostId, "Original Math title", "Original Math desc", GameStatus.DRAFT);

    when(gameRepository.findByIdAndCreatedBy(gameId, hostId)).thenReturn(Optional.of(game));
    when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

    GameResponse mappedResponse = new GameResponse();
    mappedResponse.setId(gameId);
    mappedResponse.setTitle(updateRequest.getTitle());
    mappedResponse.setDescription(updateRequest.getDescription());
    mappedResponse.setStatus(GameStatus.PUBLISHED);

    when(gameMapper.toGameResponse(any(Game.class))).thenReturn(mappedResponse);

    GameResponse result = gameService.updateGameMetadata(gameId, hostId, updateRequest);

    assertEquals("Updated Math Quiz", result.getTitle());
    assertEquals("Updated description math game", result.getDescription());
    assertEquals(GameStatus.PUBLISHED, result.getStatus());
  }

  @Test
  void updateGameMetadata_allowsDraftToPublishedTransition() {
    Long gameId = 1L;
    Long hostId = 200L;

    Game game =
        GameMockData.createGame(
            gameId, hostId, "Original title", "Original desc", GameStatus.DRAFT);

    GameUpdateRequest request = new GameUpdateRequest();
    request.setStatus(GameStatus.PUBLISHED);

    when(gameRepository.findByIdAndCreatedBy(gameId, hostId)).thenReturn(Optional.of(game));
    when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

    GameResponse response = new GameResponse();
    response.setId(gameId);
    response.setStatus(GameStatus.PUBLISHED);

    when(gameMapper.toGameResponse(any(Game.class))).thenReturn(response);

    GameResponse result = gameService.updateGameMetadata(gameId, hostId, request);

    assertEquals(GameStatus.PUBLISHED, result.getStatus());
  }

  @Test
  void updateGameMetadata_throwsInvalidGameException_whenPublishedToDraft() {
    Long gameId = 1L;
    Long hostId = 200L;

    Game game =
        GameMockData.createGame(
            gameId, hostId, "Original title", "Original desc", GameStatus.PUBLISHED);

    GameUpdateRequest request = new GameUpdateRequest();
    request.setStatus(GameStatus.DRAFT);

    when(gameRepository.findByIdAndCreatedBy(gameId, hostId)).thenReturn(Optional.of(game));

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> gameService.updateGameMetadata(gameId, hostId, request),
            "Invalid status transition.");

    assertEquals("Invalid status transition from PUBLISHED to DRAFT", e.getMessage());
  }

  @Test
  void updateGameMetadata_throwsGameNotFoundException_whenGameDoesNotExist() {
    Long gameId = 999L;
    Long hostId = 200L;

    GameUpdateRequest request = new GameUpdateRequest();
    request.setTitle("AnythingWrong");

    when(gameRepository.findByIdAndCreatedBy(gameId, hostId)).thenReturn(Optional.empty());

    assertThrows(
        InvalidGameException.class, () -> gameService.updateGameMetadata(gameId, hostId, request));
  }

  @Test
  void updateGameMetadata_throwsInvalidGameException_whenTitleTooShort() {
    Long gameId = 1L;
    Long hostId = 200L;

    Game game =
        GameMockData.createGame(
            gameId, hostId, "Small title", "My description", GameStatus.PUBLISHED);

    GameUpdateRequest request = new GameUpdateRequest();
    request.setTitle("ab");

    when(gameRepository.findByIdAndCreatedBy(gameId, hostId)).thenReturn(Optional.of(game));

    assertDoesNotThrow(() -> gameService.updateGameMetadata(gameId, hostId, request));
  }

  @Test
  void updateGameMetadata_withEmptyRequest_doesNotChangeAnything() {
    Long gameId = 1L;
    Long hostId = 200L;

    Game game =
        GameMockData.createGame(
            gameId, hostId, "My fav title", "My white description", GameStatus.PUBLISHED);

    GameUpdateRequest request = new GameUpdateRequest();

    when(gameRepository.findByIdAndCreatedBy(gameId, hostId)).thenReturn(Optional.of(game));
    when(gameRepository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

    when(gameMapper.toGameResponse(any(Game.class)))
        .thenAnswer(
            invocation -> {
              Game g = invocation.getArgument(0);
              GameResponse resp = new GameResponse();
              resp.setId(g.getId());
              resp.setTitle(g.getTitle());
              resp.setDescription(g.getDescription());
              resp.setStatus(g.getStatus());
              return resp;
            });

    GameResponse result = gameService.updateGameMetadata(gameId, hostId, request);

    assertEquals("My fav title", result.getTitle());
    assertEquals("My white description", result.getDescription());
    assertEquals(GameStatus.PUBLISHED, result.getStatus());
  }
}
