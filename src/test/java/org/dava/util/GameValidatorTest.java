package org.dava.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.dava.dto.GameRequest;
import org.dava.dto.QuestionRequest;
import org.dava.enumeration.GameStatus;
import org.dava.exception.InvalidGameException;
import org.dava.mock.GameRequestMockData;
import org.dava.mock.QuestionRequestMockData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class GameValidatorTest {

  @Test
  void checkIntegerInputWithInvalidNumberThrowsIllegalArgumentException() {
    // Arrange
    Integer negativeInput = -1;

    // Act & Assert
    Assertions.assertAll(
        "Check Invalid Integer Assertions",
        () ->
            assertThrows(
                IllegalArgumentException.class,
                () -> GameValidator.checkIntegerInput(null, "input")),
        () ->
            assertThrows(
                IllegalArgumentException.class,
                () -> GameValidator.checkIntegerInput(negativeInput, "input")));
  }

  @Test
  void checkIntegerInputWithValidNumberReturnsVoid() {
    Assertions.assertDoesNotThrow(() -> GameValidator.checkIntegerInput(0, "input"));
  }

  @Test
  void processGameRequestWhenNullThrowsInvalidGameException() {
    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processGameRequest(null),
            "Null game request must throw InvalidGameException");
    assertExceptionMessageNotNull(e);
  }

  @Test
  void processGameRequestWhenTitleBlankThrowsInvalidGameException() {
    GameRequest request = GameRequestMockData.getValidGameRequest();
    request.setTitle(" ");

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processGameRequest(request),
            "Blank title must be rejected");

    assertTrue(
        e.getMessage().contains("title") || !e.getMessage().isEmpty(),
        "Message should mention invalid title or be non-empty");
  }

  @Test
  void processGameRequestWhenTitleNullThrowsInvalidGameException() {
    GameRequest request = GameRequestMockData.getValidGameRequest();
    request.setTitle(null);

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processGameRequest(request),
            "Null title must be rejected");

    assertTrue(
        e.getMessage().contains("title") || !e.getMessage().isEmpty(),
        "Message should mention invalid title or be non-empty");
  }

  @Test
  void processGameRequestWhenNoQuestionsThrowsInvalidGameException() {
    GameRequest request = GameRequestMockData.getValidGameRequest();
    request.setQuestions(List.of());

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processGameRequest(request),
            "Request with no questions must be rejected");
    assertExceptionMessageNotNull(e);
  }

  @Test
  void processGameRequestWhenQuestionsNullThrowsInvalidGameException() {
    GameRequest request = GameRequestMockData.getValidGameRequest();
    request.setQuestions(null);

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processGameRequest(request),
            "Request with no questions must be rejected");
    assertExceptionMessageNotNull(e);
  }

  @Test
  void processGameRequestWhenQuestionsCountMismatchThrowsInvalidGameException() {
    GameRequest request = GameRequestMockData.getValidGameRequest();
    request.setQuestionCount(100);

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processGameRequest(request),
            "Request with no question count mismatch must be rejected");
    assertExceptionMessageNotNull(e);
  }

  @Test
  void processGameRequestWhenStatusNullSetsDefaultDraft() {
    GameRequest request = GameRequestMockData.getValidGameRequest();
    request.setStatus(null);

    GameValidator.processGameRequest(request);

    assertEquals(GameStatus.DRAFT, request.getStatus(), "Status must default to DRAFT when null");
  }

  @Test
  void processQuestionWhenTextBlankThrowsInvalidGameException() {
    QuestionRequest q = QuestionRequestMockData.getValidQuestionRequest();
    q.setText(" ");

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processQuestionFromRequest(q),
            "Question with blank text must be rejected");

    assertTrue(
        e.getMessage().toLowerCase().contains("text") || !e.getMessage().isEmpty(),
        "Message should mention question text or be non-empty");
  }

  @Test
  void processQuestionWhenTextNullThrowsInvalidGameException() {
    QuestionRequest q = QuestionRequestMockData.getValidQuestionRequest();
    q.setText(null);

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processQuestionFromRequest(q),
            "Question with null text must be rejected");

    assertTrue(
        e.getMessage().toLowerCase().contains("text") || !e.getMessage().isEmpty(),
        "Message should mention question text or be non-empty");
  }

  @Test
  void processQuestionNullOptionsThrowsInvalidGameException() {
    QuestionRequest q = QuestionRequestMockData.getValidQuestionRequest();
    q.setOptions(null);
    q.setCorrectOptionIndex(0);

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processQuestionFromRequest(q),
            "Question with null options list must be rejected");
    assertExceptionMessageNotNull(e);
  }

  @Test
  void processQuestionWithFewOptionsThrowsInvalidGameException() {
    QuestionRequest q = QuestionRequestMockData.getValidQuestionRequest();
    q.setOptions(List.of("1"));
    q.setCorrectOptionIndex(0);

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processQuestionFromRequest(q),
            "Question with less than 2 options must be rejected");
    assertExceptionMessageNotNull(e);
  }

  @Test
  void processQuestionWithTooManyOptionsThrowsInvalidGameException() {
    QuestionRequest q = QuestionRequestMockData.getValidQuestionRequest();
    q.setOptions(List.of("1", "2", "3", "4", "5", "6", "7"));
    q.setCorrectOptionIndex(0);

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processQuestionFromRequest(q),
            "Question with more than 6 options must be rejected");
    assertExceptionMessageNotNull(e);
  }

  @Test
  void processQuestionWhenCorrectIndexNullThrowsInvalidGameException() {
    QuestionRequest q = QuestionRequestMockData.getValidQuestionRequest();
    q.setCorrectOptionIndex(null);

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processQuestionFromRequest(q),
            "Null correctOptionIndex must be rejected");
    assertExceptionMessageNotNull(e);
  }

  @Test
  void processQuestionWhenCorrectIndexOutOfBoundsThrowsInvalidGameException() {
    QuestionRequest q = QuestionRequestMockData.getValidQuestionRequest();
    q.setOptions(List.of("1", "2", "3"));
    q.setCorrectOptionIndex(3);

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processQuestionFromRequest(q),
            "Out-of-bounds correctOptionIndex must be rejected");
    assertExceptionMessageNotNull(e);
  }

  @Test
  void processQuestionWhenCorrectIndexNegativeThrowsInvalidGameException() {
    QuestionRequest q = QuestionRequestMockData.getValidQuestionRequest();
    q.setOptions(List.of("1", "2", "3"));
    q.setCorrectOptionIndex(-1);

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processQuestionFromRequest(q),
            "Out-of-bounds correctOptionIndex must be rejected");
    assertExceptionMessageNotNull(e);
  }

  @Test
  void processQuestionWhenImageUrlNullThrowsInvalidGameException() {
    QuestionRequest q = QuestionRequestMockData.getValidQuestionRequest();
    q.setImageUrl(null);

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processQuestionFromRequest(q),
            "Blank imageUrl must be rejected");
    assertExceptionMessageNotNull(e);
  }

  @Test
  void processQuestionWhenImageUrlBlankThrowsInvalidGameException() {
    QuestionRequest q = QuestionRequestMockData.getValidQuestionRequest();
    q.setImageUrl(" ");

    InvalidGameException e =
        assertThrows(
            InvalidGameException.class,
            () -> GameValidator.processQuestionFromRequest(q),
            "Blank imageUrl must be rejected");
    assertExceptionMessageNotNull(e);
  }

  @Test
  void processQuestionValidQuestionPassesWithoutException() {
    QuestionRequest q = QuestionRequestMockData.getValidQuestionRequest();

    assertDoesNotThrow(
        () -> GameValidator.processQuestionFromRequest(q), "Valid question must not throw");
  }

  private void assertExceptionMessageNotNull(InvalidGameException e) {
    assertNotNull(e.getMessage(), "Exception message should not be null");
  }
}
