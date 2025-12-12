package org.dava.exception;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionMessage {

  private String timestamp;
  private String error;
  private String message;

  public static ExceptionMessage of(String error, String message) {
    return new ExceptionMessage(Instant.now().toString(), error, message);
  }
}
