package org.dava.mapper;

import org.dava.domain.Game;
import org.dava.domain.Player;
import org.dava.dto.CreatePlayerRequest;
import org.dava.dto.CreatePlayerResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting between player-related DTOs and entities. Uses MapStruct for
 * compile-time generated implementations.
 */
@Mapper(componentModel = "spring")
public interface PlayerMapper {

  /** Maps a Player entity to a CreatePlayerResponse DTO. */
  @Mapping(source = "player.game.id", target = "gameId")
  @Mapping(source = "player.created_at", target = "created_at")
  CreatePlayerResponse toCreatePlayerResponse(Player player);

  /**
   * Maps a CreatePlayerRequest to a Player entity. Note: game is manually added because MapStruct
   * cannot infer nested associations.
   */
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "score", constant = "0")
  @Mapping(target = "created_at", ignore = true)
  @Mapping(target = "game", source = "game")
  Player toPlayer(CreatePlayerRequest request, Game game);
}
