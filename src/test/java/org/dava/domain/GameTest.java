package org.dava.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.dava.mock.GameMockData;
import org.junit.jupiter.api.Test;

class GameTest {

  @Test
  void getQuestionCountReturnsZeroWhenQuestionsNull() {
    Game game = GameMockData.getGameWithNullQuestions();

    Integer count = game.getQuestionCount();

    assertEquals(0, count, "Question count should be 0 when questions list is null");
  }

  @Test
  void getQuestionCountReturnsZeroWhenQuestionsEmpty() {
    Game game = GameMockData.getGameWithNoQuestions();

    Integer count = game.getQuestionCount();

    assertEquals(0, count, "Question count should be 0 when questions list is empty");
  }

  @Test
  void getQuestionCountReturnsSizeOfQuestionsList() {
    Game game = GameMockData.getGameWithValidNumberOfQuestions();

    Integer count = game.getQuestionCount();

    assertEquals(2, count, "Question count should equal the number of questions added");
  }
}
