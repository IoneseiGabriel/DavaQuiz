package org.dava.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.NoSuchElementException;
import org.dava.dao.GameRepository;
import org.dava.dao.PlayerRepository;
import org.dava.domain.Game;
import org.dava.domain.Player;
import org.dava.dto.CreatePlayerRequest;
import org.dava.dto.CreatePlayerResponse;
import org.dava.mapper.PlayerMapper;
import org.dava.mock.GameMockData;
import org.dava.mock.PlayerMockData;
import org.dava.validator.PlayerValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerServiceImplTest {

  @Mock private PlayerRepository playerRepository;

  @Mock private GameRepository gameRepository;

  @Mock private PlayerValidator playerValidator;

  @Mock private PlayerMapper playerMapper;

  @InjectMocks private PlayerServiceImpl playerService;

  @Test
  void createPlayer_validRequest_savesAndReturnsResponse() {
    CreatePlayerRequest request = PlayerMockData.getValidCreatePlayerRequest();
    Game game = GameMockData.getValidGameList().get(0);

    Player mappedPlayer = PlayerMockData.getNewPlayer(game);
    Player savedPlayer = PlayerMockData.getSavedPlayer(game);

    CreatePlayerResponse expectedResponse = PlayerMockData.getCreatePlayerResponse(savedPlayer);

    doNothing().when(playerValidator).validateRequest(request);
    when(gameRepository.getReferenceById(request.getGameId())).thenReturn(game);
    when(playerMapper.toPlayer(request, game)).thenReturn(mappedPlayer);
    when(playerRepository.save(mappedPlayer)).thenReturn(savedPlayer);
    when(playerMapper.toCreatePlayerResponse(savedPlayer)).thenReturn(expectedResponse);

    CreatePlayerResponse response = playerService.createPlayer(request);

    assertEquals(expectedResponse.getId(), response.getId());
    assertEquals(expectedResponse.getGameId(), response.getGameId());
    assertEquals(expectedResponse.getName(), response.getName());
    assertEquals(expectedResponse.getScore(), response.getScore());

    verify(playerValidator).validateRequest(request);
    verify(gameRepository).getReferenceById(request.getGameId());
    verify(playerMapper).toPlayer(request, game);
    verify(playerRepository).save(mappedPlayer);
    verify(playerMapper).toCreatePlayerResponse(savedPlayer);
  }

  @Test
  void createPlayer_nullRequest_throwsIllegalArgumentException() {
    doThrow(new IllegalArgumentException("Invalid player request"))
        .when(playerValidator)
        .validateRequest(null);

    assertThrows(IllegalArgumentException.class, () -> playerService.createPlayer(null));

    verify(playerValidator, atMostOnce()).validateRequest(null);
    verifyNoInteractions(gameRepository);
    verifyNoInteractions(playerRepository);
    verifyNoInteractions(playerMapper);
  }

  @Test
  void createPlayer_gameNotFound_throwsNoSuchElementException() {
    CreatePlayerRequest request = PlayerMockData.getValidCreatePlayerRequest();

    doNothing().when(playerValidator).validateRequest(request);
    doThrow(new NoSuchElementException("Game not found"))
        .when(gameRepository)
        .getReferenceById(request.getGameId());

    assertThrows(NoSuchElementException.class, () -> playerService.createPlayer(request));

    verify(playerValidator, atMostOnce()).validateRequest(request);
    verify(gameRepository, atMostOnce()).getReferenceById(request.getGameId());
    verifyNoInteractions(playerRepository);
    verifyNoInteractions(playerMapper);
  }
}
