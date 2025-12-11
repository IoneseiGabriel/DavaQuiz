package org.dava.service;

import java.util.Map;
import org.dava.dto.GameRequest;
import org.dava.dto.GameUpdateRequest;
import org.dava.response.GameResponse;
import org.dava.response.PageResponse;

public interface GameService {
  GameResponse createGame(GameRequest gameRequest, Long userId);

  PageResponse<GameResponse> getAll(int page, int size, Map<String, Object> filters);

  GameResponse updateGameMetadata(Long gameId, Long userId, GameUpdateRequest request);
}
