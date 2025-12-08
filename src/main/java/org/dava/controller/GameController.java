package org.dava.controller;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.dava.response.GameResponse;
import org.dava.response.PageResponse;
import org.dava.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** REST controller that exposes endpoints for managing and retrieving game resources. */
@RestController
@RequestMapping("/api/games")
@AllArgsConstructor
public class GameController {
  private final GameService gameService;

  /**
   * Retrieves a paginated list of games, optionally filtered by the provided query parameters.
   *
   * <p>Query Examples:
   *
   * <pre>
   *     Retrieves the second page (page index = 1) with up to 20 games,
   *     filtered by title and status:
   *     /api/games?page=1&size=20&title=temple&status=DRAFT
   *
   *     Retrieves all games for the first page using default pagination:
   *     /api/games
   * </pre>
   *
   * @param page the page number; defaults to {@code 0}
   * @param size the number of items per page; defaults to {@code 10}
   * @param filters a map of query parameters used as filtering criteria, where each entry
   *     represents a {@code field=value} pair; includes pagination parameters, but these are
   *     removed in the service layer
   * @return HTTP 200 with a response containing a {@link PageResponse} of {@link GameResponse}
   *     objects that match the pagination and filter criteria
   */
  @GetMapping
  public ResponseEntity<@NonNull PageResponse<GameResponse>> getAll(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam Map<String, Object> filters) {

    return ResponseEntity.ok(gameService.getAll(page, size, filters));
  }
}
