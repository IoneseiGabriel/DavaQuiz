package org.dava.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;
import lombok.NonNull;
import org.dava.dao.GameRepository;
import org.dava.domain.Game;
import org.dava.dto.GameUpdateRequest;
import org.dava.enumeration.GameStatus;
import org.dava.exception.InvalidGameException;
import org.dava.mapper.GameMapper;
import org.dava.mock.GameMockData;
import org.dava.mock.PageMockData;
import org.dava.response.GameResponse;
import org.dava.response.PageResponse;
import org.dava.util.GameValidator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

  @Mock private GameRepository gameRepository;

  @Mock private GameMapper gameMapper;

  @Mock private GameValidator gameValidator;

  @InjectMocks private GameServiceImpl gameService;

  private final int pageNumber = 1;

  private final int pageSize = 10;

  private Pageable pageable;

  private Map<String, Object> filters;

  private List<Game> gameList;

  private List<GameResponse> gameResponseList;

  private GameUpdateRequest updateRequest;

  @BeforeEach
  void setUp() {
    pageable = PageRequest.of(pageNumber, pageSize);
    filters = Map.of("title", "Speed", "status", "DRAFT");
    gameList = GameMockData.getValidGameList();
    gameResponseList =
        GameMockData.getValidGameList().stream().map(GameMockData::getGameResponse).toList();

    updateRequest = new GameUpdateRequest();
    updateRequest.setTitle("Updated Math Quiz");
    updateRequest.setDescription("Updated description math game");
    updateRequest.setStatus(GameStatus.PUBLISHED);
  }

  @Test
  void getAllInvalidPageOrSizeNumberThrowsIllegalArgumentException() {
    Assertions.assertAll(
        "Check Invalid Available Fields Assertions",
        () -> assertThrows(IllegalArgumentException.class, () -> gameService.getAll(-1, 0, null)),
        () -> assertThrows(IllegalArgumentException.class, () -> gameService.getAll(0, -1, null)));
  }

  @Test
  void getAllWithUnreachablePageNumberThrowsNoSuchElementException() {
    // Arrange
    Map<String, Object> emptyFilters = new HashMap<>();
    when(gameRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(List.of()));

    // Act & Assert
    Assertions.assertThrows(
        NoSuchElementException.class,
        () -> gameService.getAll(10, pageSize, emptyFilters),
        "Throws NoSuchElementException because pageNumber (10) >= totalPageNumber (0)");
  }

  @Test
  void getAllWithValidPaginationAndFiltersReturnsGamePageResponse() {
    // Arrange
    Page<@NonNull Game> gamePage = new PageImpl<>(gameList, pageable, gameList.size());

    when(gameRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(gamePage);

    PageResponse<GameResponse> expectedResponse =
        PageMockData.getPageResponse(new PageImpl<>(gameResponseList), filters.keySet());
    when(gameMapper.toResponsePage(gamePage, filters.keySet())).thenReturn(expectedResponse);

    // Act
    PageResponse<GameResponse> result = gameService.getAll(pageNumber, pageSize, filters);

    // Assert
    Assertions.assertEquals(
        expectedResponse,
        result,
        "Result must be equal to a PageResponse containing a list of 3 GameResponse objects");
    verify(gameRepository, atMostOnce()).findAll(any(Specification.class), eq(pageable));
    verify(gameMapper, atMostOnce()).toResponsePage(gamePage, filters.keySet());
  }

  @Test
  void getAllWithPageNumberZeroAndEmptyGameListReturnsEmptyPage() {
    // Arrange
    Page<@NonNull Game> gamePage = new PageImpl<>(List.of());

    when(gameRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(gamePage);

    PageResponse<GameResponse> expectedResponse =
        PageMockData.getPageResponse(new PageImpl<>(List.of()), filters.keySet());
    when(gameMapper.toResponsePage(gamePage, filters.keySet())).thenReturn(expectedResponse);

    // Act & Assert
    PageResponse<GameResponse> result = gameService.getAll(0, pageSize, filters);

    Assertions.assertEquals(
        expectedResponse, result, "The result must be a PageResponse containing an empty list");
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

    doThrow(new InvalidGameException("Invalid status transition"))
        .when(gameValidator)
        .handleStatusTransition(game, GameStatus.DRAFT);

    assertThrows(
        InvalidGameException.class, () -> gameService.updateGameMetadata(gameId, hostId, request));
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
