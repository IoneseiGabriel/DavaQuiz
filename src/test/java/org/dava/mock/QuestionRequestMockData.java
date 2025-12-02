package org.dava.mock;

import java.util.List;
import org.dava.dto.QuestionRequest;

public class QuestionRequestMockData {
  public static QuestionRequest getValidQuestionRequest() {
    return new QuestionRequest("Q", List.of("1", "2", "3"), 1, "https://example.com/img.png");
  }
}
