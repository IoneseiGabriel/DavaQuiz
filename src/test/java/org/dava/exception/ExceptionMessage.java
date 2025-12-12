package org.dava.exception;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import org.junit.jupiter.api.Test;

class ExceptionMessageTest {

  @Test
  void allArgsConstructorShouldSetFields() {
    String ts = "2024-01-01T00:00:00Z";
    ExceptionMessage msg = new ExceptionMessage(ts, "ERR", "Something went wrong");

    assertEquals(ts, msg.getTimestamp(), "Timestamps should be equal");
    assertEquals("ERR", msg.getError(), "Error messages should be the same");
    assertEquals("Something went wrong", msg.getMessage(), "Message should be the same");
  }

  @Test
  void noArgsConstructorShouldInitializeNullFields() {
    ExceptionMessage msg = new ExceptionMessage();

    assertNull(msg.getTimestamp());
    assertNull(msg.getError());
    assertNull(msg.getMessage());
  }

  @Test
  void settersShouldUpdateFields() {
    ExceptionMessage msg = new ExceptionMessage();

    msg.setTimestamp("2024-01-02T10:00:00Z");
    msg.setError("BAD_REQUEST");
    msg.setMessage("Invalid input");

    assertEquals("2024-01-02T10:00:00Z", msg.getTimestamp(), "Timestamps should be equal");
    assertEquals("BAD_REQUEST", msg.getError(), "Error messages should be the same");
    assertEquals("Invalid input", msg.getMessage(), "Message should be the same");
  }

  @Test
  void ofShouldGenerateValidTimestampAndSetFields() {
    ExceptionMessage msg = ExceptionMessage.of("ERR_CODE", "Failure happened");

    assertNotNull(msg.getTimestamp(), "Timestamp should not be null");
    assertNotNull(msg.getError());
    assertNotNull(msg.getMessage());

    assertEquals("ERR_CODE", msg.getError());
    assertEquals("Failure happened", msg.getMessage());

    assertDoesNotThrow(
        () -> Instant.parse(msg.getTimestamp()),
        "Timestamp should be parsable as an ISO-8601 instant");
  }

  @Test
  void ofShouldUseCurrentTimestamp() {
    Instant before = Instant.now();
    ExceptionMessage msg = ExceptionMessage.of("ERR", "Something bad");
    Instant after = Instant.now();

    Instant ts = Instant.parse(msg.getTimestamp());

    assertTrue(
        !ts.isBefore(before) && !ts.isAfter(after),
        "Timestamp must be between method call start and end time");
  }
}
