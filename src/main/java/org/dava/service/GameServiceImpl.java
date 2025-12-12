package org.dava.service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.dava.dao.GameRepository;
import org.dava.dao.GameSpecification;
import org.dava.domain.Game;
import org.dava.domain.Question;
import org.dava.dto.GameRequest;
import org.dava.dto.GameUpdateRequest;
import org.dava.dto.QuestionRequest;
import org.dava.exception.InvalidGameException;
import org.dava.mapper.GameMapper;
import org.dava.mapper.QuestionMapper;
import org.dava.response.GameResponse;
import org.dava.response.PageResponse;
import org.dava.response.QuestionResponse;
import org.dava.util.FilteringHelper;
import org.dava.util.GameValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

/**
 * An implementation for the {@link GameService} interface. Provides methods for retrieving,
 * creating & updating games.
 */
@Service
@RequiredArgsConstructor
public class GameServiceImpl implements GameService {
  private final GameRepository gameRepository;

  private final GameMapper gameMapper;
  private final QuestionMapper questionMapper;

  /**
   * Retrieves a paginated list of games, optionally filtered by several fields.
   *
   * <p>Supported filters are included in the {@link GameServiceImpl#getFilterFields()}. If no
   * {@code filters} are provided, all games are returned, based on the provided {@code pagination
   * parameters}.
   *
   * @param page the page number
   * @param size the size of the page
   * @param filters a map of filters written as {@code field=value} pairs
   * @return a {@link PageResponse} containing the requested page of {@link GameResponse} objects
   */
  public PageResponse<GameResponse> getAll(int page, int size, Map<String, Object> filters) {
    GameValidator.checkIntegerInput(page, "page");
    GameValidator.checkIntegerInput(size, "size");

    Map<String, Object> parsedFilters =
        FilteringHelper.parseFilters(new ConcurrentHashMap<>(filters), getFilterFields());

    Pageable pageable = PageRequest.of(page, size);
    Page<@NonNull Game> games;

    Specification<@NonNull Game> gameSpec = GameSpecification.createSpecification(parsedFilters);

    if (gameSpec == null) {
      games = gameRepository.findAll(pageable);
    } else {
      games = gameRepository.findAll(gameSpec, pageable);
    }

    if (page != 0 && page >= games.getTotalPages()) {
      throw new NoSuchElementException(String.format("Page with number %d was not found.", page));
    }

    return gameMapper.toResponsePage(games, filters.keySet());
  }

  /**
   * Creates a new {@link Game} entity based on the provided request payload and user identifier,
   * persists it, and returns a mapped {@link GameResponse}.
   *
   * <p>The method performs several steps:
   *
   * <ul>
   *   <li>Validates the incoming {@link GameRequest} and its nested {@link QuestionResponse}
   *       objects.
   *   <li>Constructs a new {@link Game} entity and populates its fields using {@link
   *       DomainFieldsHelper}.
   *   <li>Creates and populates {@link Question} entities for each question in the request.
   *   <li>Persists the assembled game using {@link GameRepository}.
   *   <li>Converts the saved entity into a {@link GameResponse} via {@link GameMapper}.
   * </ul>
   *
   * @param request the incoming payload containing game details and associated questions
   * @param userId the identifier of the user creating the game
   * @return a {@link GameResponse} representation of the newly created and persisted game
   */
  @Override
  public GameResponse createGame(GameRequest request, Long userId) {

    GameValidator.processGameRequest(request);

    Game newGame = gameMapper.toEntity(request, userId);

    for (QuestionRequest qReq : request.getQuestions()) {
      GameValidator.processQuestionFromRequest(qReq);

      Question q = questionMapper.toEntity(qReq);

      newGame.addQuestion(q);
    }

    Game saved = gameRepository.save(newGame);

    return gameMapper.toGameResponse(saved);
  }

  /**
   * Updates a game's metadata (title, description, status) for the given game ID and host.
   *
   * <p>Business rules:
   *
   * <ul>
   *   <li>Only the host who created the game may update it.
   *   <li>Status transitions allowed: DRAFT → PUBLISHED.
   *   <li>Status transitions forbidden: PUBLISHED → DRAFT.
   * </ul>
   *
   * @param gameId ID of the game to update
   * @param userId ID of the user performing the update (must match createdBy)
   * @param request Partial update request containing metadata fields
   * @return the updated {@link Game} instance
   * @throws InvalidGameException if the game does not exist
   */
  @Override
  public GameResponse updateGameMetadata(Long gameId, Long userId, GameUpdateRequest request) {
    Game game =
        gameRepository
            .findByIdAndCreatedBy(gameId, userId)
            .orElseThrow(() -> new InvalidGameException("Game not found"));

    if (request.getDescription() != null) {
      game.setDescription(request.getDescription());
    }

    if (request.getStatus() != null) {
      GameValidator.handleStatusTransition(game, request.getStatus());
      game.setStatus(request.getStatus());
    }

    game.setUpdatedAt(LocalDateTime.now());

    Game saved = gameRepository.save(game);
    return gameMapper.toGameResponse(saved);
  }

  private static Set<String> getFilterFields() {
    return Set.of("status", "createdAt", "updatedAt", "createdBy", "title");
  }
}
