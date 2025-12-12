package org.dava.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Represents a player participating in a specific game. Each player is associated with exactly one
 * Game (relation N:1), has a display name, an initial score (default 0), and an auto-generated
 * timestamp for storing the moment of creation of each player.
 */
@Builder
@Data
@Entity
@Table(
    name = "player",
    indexes = {@Index(name = "index_player_game_id", columnList = "game_id")})
public class Player {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "game_id", nullable = false)
  @JsonIgnore
  private Game game;

  @Column(name = "name", length = 100, nullable = false)
  private String name;

  @Column(name = "score", nullable = false)
  private Integer score;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime created_at;
}
