package org.dava.mock;

import java.time.LocalDateTime;
import org.dava.domain.Game;
import org.dava.domain.Player;
import org.dava.dto.CreatePlayerRequest;
import org.dava.dto.CreatePlayerResponse;

public class PlayerMockData {
  private static final Long DEFAULT_GAME_ID = 1L;

  public static CreatePlayerRequest getValidCreatePlayerRequest() {

    return CreatePlayerRequest.builder().gameId(1L).name("Cristi").build();
  }

  public static CreatePlayerRequest getTooShortNameRequest() {
    return CreatePlayerRequest.builder().gameId(1L).name("ab").build();
  }

  public static Player getNewPlayer(Game game) {
    return Player.builder().name("Cristi").game(game).score(0).build();
  }

  public static Player getSavedPlayer(Game game) {
    return Player.builder()
        .id(10L)
        .name("Cristi")
        .game(game)
        .score(0)
        .created_at(LocalDateTime.now())
        .build();
  }

  public static CreatePlayerResponse getCreatePlayerResponse(Player player) {
    return CreatePlayerResponse.builder()
        .id(player.getId())
        .gameId(player.getGame().getId())
        .name(player.getName())
        .score(player.getScore())
        .created_at(player.getCreated_at())
        .build();
  }

  public static CreatePlayerRequest getNullNameRequest() {
    return CreatePlayerRequest.builder().gameId(DEFAULT_GAME_ID).name(null).build();
  }
}
