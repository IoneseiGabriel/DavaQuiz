package org.dava.validator;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.NoSuchElementException;
import java.util.Optional;
import org.dava.dao.GameRepository;
import org.dava.domain.Game;
import org.dava.dto.CreatePlayerRequest;
import org.dava.mock.GameMockData;
import org.dava.mock.PlayerMockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PlayerValidatorImplTest {

  private static final Long GAME_ID = 1L;

  @Mock private GameRepository gameRepository;

  @InjectMocks private PlayerValidatorImpl validator;

  @Test
  void validateRequest_nullRequest_throwsIllegalArgumentException() {

    assertThrows(IllegalArgumentException.class, () -> validator.validateRequest(null));

    verifyNoInteractions(gameRepository);
  }

  @Test
  void validateRequest_shortName_throwsIllegalArgumentException() {
    CreatePlayerRequest request = PlayerMockData.getTooShortNameRequest();

    assertThrows(IllegalArgumentException.class, () -> validator.validateRequest(request));

    verifyNoInteractions(gameRepository);
  }

  @Test
  void validateRequest_nullName_throwsIllegalArgumentException() {
    CreatePlayerRequest request = PlayerMockData.getNullNameRequest();

    assertThrows(IllegalArgumentException.class, () -> validator.validateRequest(request));

    verifyNoInteractions(gameRepository);
  }

  @Test
  void validateRequest_gameNotFound_throwsNoSuchElementException() {
    CreatePlayerRequest request = PlayerMockData.getValidCreatePlayerRequest();

    when(gameRepository.findById(GAME_ID)).thenReturn(Optional.empty());

    assertThrows(NoSuchElementException.class, () -> validator.validateRequest(request));

    verify(gameRepository).findById(GAME_ID);
  }

  @Test
  void validateRequest_gameNotPublished_throwsIllegalArgumentException() {
    CreatePlayerRequest request = PlayerMockData.getValidCreatePlayerRequest();
    Game notPublishedGame = GameMockData.getValidGameList().get(1);

    when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(notPublishedGame));

    assertThrows(IllegalArgumentException.class, () -> validator.validateRequest(request));

    verify(gameRepository).findById(GAME_ID);
  }

  @Test
  void validateRequest_validRequest_doesNotThrow() {
    CreatePlayerRequest request = PlayerMockData.getValidCreatePlayerRequest();
    Game publishedGame = GameMockData.getValidGameList().get(0);

    when(gameRepository.findById(GAME_ID)).thenReturn(Optional.of(publishedGame));

    assertDoesNotThrow(() -> validator.validateRequest(request));

    verify(gameRepository).findById(GAME_ID);
  }
}
