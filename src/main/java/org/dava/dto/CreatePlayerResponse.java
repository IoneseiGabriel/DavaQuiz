package org.dava.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

/**
 * Response DTO returned after successfully creating a player. Contains basic player details and the
 * creation timestamp.
 */
@Builder
@Data
public class CreatePlayerResponse {
  private Long id;
  private Long gameId;
  private String name;
  private Integer score;
  private LocalDateTime created_at;
}
