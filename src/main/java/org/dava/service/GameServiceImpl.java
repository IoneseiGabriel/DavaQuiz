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
import org.dava.dto.GameUpdateRequest;
import org.dava.exception.InvalidGameException;
import org.dava.mapper.GameMapper;
import org.dava.response.GameResponse;
import org.dava.response.PageResponse;
import org.dava.util.FilteringHelper;
import org.dava.util.GameValidator;
import org.dava.util.ValidationUtil;
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

  private final GameValidator gameValidator;

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
    ValidationUtil.checkIntegerInput(page, "page");
    ValidationUtil.checkIntegerInput(size, "size");

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

  private static Set<String> getFilterFields() {
    return Set.of("status", "createdAt", "updatedAt", "createdBy", "title");
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
      gameValidator.handleStatusTransition(game, request.getStatus());
      game.setStatus(request.getStatus());
    }

    game.setUpdatedAt(LocalDateTime.now());

    Game saved = gameRepository.save(game);
    return gameMapper.toGameResponse(saved);
  }
}
