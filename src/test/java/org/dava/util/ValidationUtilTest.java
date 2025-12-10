package org.dava.util;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ValidationUtilTest {

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
                () -> ValidationUtil.checkIntegerInput(null, "input")),
        () ->
            assertThrows(
                IllegalArgumentException.class,
                () -> ValidationUtil.checkIntegerInput(negativeInput, "input")));
  }

  @Test
  void checkIntegerInputWithValidNumberReturnsVoid() {
    Assertions.assertDoesNotThrow(() -> ValidationUtil.checkIntegerInput(0, "input"));
  }
}
