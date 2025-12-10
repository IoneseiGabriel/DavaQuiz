package org.dava.controller;

import jakarta.validation.Valid;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.dava.dto.GameUpdateRequest;
import org.dava.response.GameResponse;
import org.dava.response.PageResponse;
import org.dava.security.JwtTokenProvider;
import org.dava.service.AuthService;
import org.dava.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
  private final AuthService authService;
  private final JwtTokenProvider jwtTokenProvider;

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

  /**
   * Partially updates a game's metadata (title, description, status).
   *
   * <p>This endpoint supports PATCH semantics, meaning only the fields provided in the request body
   * will be updated. Ownership validation is applied, allowing only the host who created the game
   * to modify it.
   *
   * <p><strong>Behavior:</strong>
   *
   * <ul>
   *   <li>Returns <code>200 OK</code> with the updated game on success
   *   <li>Returns <code>400 Bad Request</code> for invalid updates (e.g. title too short, invalid
   *       status transition)
   *   <li>Returns <code>401 Unauthorized</code> if authentication is missing
   *   <li>Returns <code>404 Not Found</code> if the game does not exist or does not belong to the
   *       authenticated host
   * </ul>
   *
   * @param id the ID of the game to update
   * @param request the metadata fields to update
   * @param authHeader auth bearer token
   * @return the updated game resource as HTTP 200
   */
  @PatchMapping("/{id}")
  public ResponseEntity<GameResponse> updateGameMetadata(
      @PathVariable Long id,
      @Valid @RequestBody GameUpdateRequest request,
      @RequestHeader("Authorization") String authHeader) {

    String token =
        authHeader.startsWith("Bearer ") ? authHeader.substring("Bearer ".length()) : null;

    Long userId = jwtTokenProvider.validateAndGetUserId(token);

    GameResponse updatedGame = gameService.updateGameMetadata(id, userId, request);

    return ResponseEntity.ok(updatedGame);
  }
}
