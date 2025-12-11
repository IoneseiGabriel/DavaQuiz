package org.dava.util;

import lombok.experimental.UtilityClass;
import org.dava.domain.Game;
import org.dava.dto.GameRequest;
import org.dava.dto.QuestionRequest;
import org.dava.enumeration.GameStatus;
import org.dava.exception.InvalidGameException;

@UtilityClass
public class GameValidator {

  public void checkIntegerInput(Integer input, String paramName) {
    if (input == null || input < 0) {
      throw new IllegalArgumentException(String.format("Invalid %s number", paramName));
    }
  }

  public void processGameRequest(GameRequest request) {
    if (request == null) {
      throw new InvalidGameException("Game request cannot be null");
    }

    if (request.getTitle() == null || request.getTitle().isBlank()) {
      throw new InvalidGameException("Game title cannot be empty");
    }

    if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
      throw new InvalidGameException("At least one question is required");
    }

    if (request.getQuestions().size() != request.getQuestionCount()) {
      throw new InvalidGameException("Question count mismatch");
    }

    if (request.getStatus() == null) {
      request.setStatus(GameStatus.DRAFT);
    }
  }

  public void processQuestionFromRequest(QuestionRequest q) {
    if (q.getText() == null || q.getText().isBlank()) {
      throw new InvalidGameException("Question text cannot be empty");
    }

    if (q.getOptions() == null || q.getOptions().size() < 2 || q.getOptions().size() > 6) {
      throw new InvalidGameException("Each question must have between two and six options");
    }

    if (q.getCorrectOptionIndex() == null) {
      throw new InvalidGameException("A correct option index is required for each question");
    }

    if (q.getCorrectOptionIndex() < 0 || q.getCorrectOptionIndex() >= q.getOptions().size()) {
      throw new InvalidGameException(
          "The correct option index is out of bounds for your question: " + q.getText());
    }

    if (q.getImageUrl() == null || q.getImageUrl().isBlank()) {
      throw new InvalidGameException("Image URL cannot be empty");
    }
  }

  public void handleStatusTransition(Game game, GameStatus requestedStatus) {
    GameStatus currentStatus = game.getStatus();

    if (currentStatus == requestedStatus) {
      return;
    }

    if (currentStatus == GameStatus.DRAFT && requestedStatus == GameStatus.PUBLISHED) {
      return;
    }

    throw new InvalidGameException(
        "Invalid status transition from " + currentStatus + " to " + requestedStatus);
  }
}
