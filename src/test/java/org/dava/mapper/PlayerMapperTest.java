package org.dava.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.dava.domain.Game;
import org.dava.domain.Player;
import org.dava.dto.CreatePlayerRequest;
import org.dava.dto.CreatePlayerResponse;
import org.dava.mock.GameMockData;
import org.dava.mock.PlayerMockData;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;

class PlayerMapperTest {

  @InjectMocks private PlayerMapper mapper = Mappers.getMapper(PlayerMapper.class);

  @Test
  void toEntity_mapsFieldsCorrectly() {
    CreatePlayerRequest request = PlayerMockData.getValidCreatePlayerRequest();
    Game game = GameMockData.getValidGameList().get(0);

    Player player = mapper.toPlayer(request, game);

    assertEquals(request.getName(), player.getName());
    assertEquals(0, player.getScore());
    assertEquals(game, player.getGame());
  }

  @Test
  void toResponse_mapsFieldsCorrectly() {
    Game game = GameMockData.getValidGameList().get(0);
    Player savedPlayer = PlayerMockData.getSavedPlayer(game);

    CreatePlayerResponse response = mapper.toCreatePlayerResponse(savedPlayer);

    assertEquals(savedPlayer.getId(), response.getId());
    assertEquals(savedPlayer.getGame().getId(), response.getGameId());
    assertEquals(savedPlayer.getName(), response.getName());
    assertEquals(savedPlayer.getScore(), response.getScore());
    assertEquals(savedPlayer.getCreated_at(), response.getCreated_at());
  }
}
