package org.dava.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * Request DTO used when creating a new player. Contains the game identifier and the player's name.
 */
@AllArgsConstructor
@Builder
@Data
public class CreatePlayerRequest {
  private Long gameId;
  private String name;
}
