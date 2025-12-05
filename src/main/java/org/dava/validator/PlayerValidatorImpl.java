package org.dava.validator;

import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.dava.dao.GameRepository;
import org.dava.domain.Game;
import org.dava.dto.CreatePlayerRequest;
import org.dava.enumeration.GameStatus;
import org.springframework.stereotype.Component;

/**
 * Validates player creation requests. Ensures that the request data is valid and that the
 * referenced game exists and allows player registration.
 */
@RequiredArgsConstructor
@Component
public class PlayerValidatorImpl implements PlayerValidator {

  private static final int MIN_NAME_LENGTH = 3;
  private static final int MAX_NAME_LENGTH = 100;
  private static final String INVALID_REQUEST_MESSAGE = "Invalid player request";
  private static final String INVALID_NAME_MESSAGE =
      "Player name should be between "
          + (MIN_NAME_LENGTH - 1)
          + " and "
          + MAX_NAME_LENGTH
          + " characters";
  private static final String GAME_NOT_FOUND_MESSAGE = "Game not found";
  private static final String GAME_NOT_OPEN_MESSAGE = "Game not open for players";

  private final GameRepository gameRepository;

  /**
   * Validates the incoming player creation request.
   *
   * @param playerRequest the request to validate
   * @throws IllegalArgumentException if the request format is invalid
   * @throws NoSuchElementException if the referenced game does not exist
   */
  @Override
  public void validateRequest(CreatePlayerRequest playerRequest) {
    validateNotNull(playerRequest);
    validateName(playerRequest.getName());

    Game game = getExistingGame(playerRequest.getGameId());
    validateGameIsOpen(game);
  }

  private void validateNotNull(CreatePlayerRequest playerRequest) {
    if (playerRequest == null) {
      throw new IllegalArgumentException(INVALID_REQUEST_MESSAGE);
    }
  }

  private void validateName(String name) {
    if (name == null || name.length() < MIN_NAME_LENGTH || name.length() > MAX_NAME_LENGTH) {
      throw new IllegalArgumentException(INVALID_NAME_MESSAGE);
    }
  }

  private Game getExistingGame(Long gameId) {
    return gameRepository
        .findById(gameId)
        .orElseThrow(() -> new NoSuchElementException(GAME_NOT_FOUND_MESSAGE));
  }

  private void validateGameIsOpen(Game game) {
    if (game.getStatus() != GameStatus.PUBLISHED) {
      throw new IllegalArgumentException(GAME_NOT_OPEN_MESSAGE);
    }
  }
}
