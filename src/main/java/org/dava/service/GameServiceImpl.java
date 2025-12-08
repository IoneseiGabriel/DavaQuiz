package org.dava.service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.dava.dao.GameRepository;
import org.dava.dao.GameSpecification;
import org.dava.domain.Game;
import org.dava.mapper.GameMapper;
import org.dava.response.GameResponse;
import org.dava.response.PageResponse;
import org.dava.util.FilteringHelper;
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
}
