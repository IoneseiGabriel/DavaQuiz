package org.dava.mock;

import java.util.List;
import org.dava.dto.GameRequest;
import org.dava.dto.QuestionRequest;
import org.dava.enumeration.GameStatus;

public class GameRequestMockData {
  public static GameRequest getValidGameRequest() {

    QuestionRequest q1 =
        new QuestionRequest(
            "First question", List.of("1", "2", "3"), 1, "https://example.com/image1.png");

    QuestionRequest q2 =
        new QuestionRequest(
            "Second question", List.of("A", "B"), 0, "https://example.com/image2.png");

    return new GameRequest(
        "Second quiz",
        "Simple test quiz",
        GameStatus.DRAFT,
        1L,
        "2025-12-02T12:30:00",
        "2025-12-02T12:30:00",
        2,
        List.of(q1, q2));
  }
}
