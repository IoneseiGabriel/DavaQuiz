package org.dava.service;

import lombok.RequiredArgsConstructor;
import org.dava.dao.GameRepository;
import org.dava.dao.PlayerRepository;
import org.dava.domain.Game;
import org.dava.domain.Player;
import org.dava.dto.CreatePlayerRequest;
import org.dava.dto.CreatePlayerResponse;
import org.dava.mapper.PlayerMapper;
import org.dava.validator.PlayerValidator;
import org.springframework.stereotype.Service;

/**
 * Service implementation responsible for handling player-related operations. Provides the logic for
 * creating a new player.
 */
@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {

  private final PlayerRepository playerRepository;
  private final GameRepository gameRepository;
  private final PlayerValidator playerValidator;
  private final PlayerMapper playerMapper;

  /**
   * Creates a new player based on the incoming request. The method performs validation on the
   * request, retrieving the game by game id, building and saving of the new player entity and then
   * returning the mapped created player response
   *
   * @param playerRequest the incoming player creation request
   * @return the created player wrapped in a response DTO
   */
  @Override
  public CreatePlayerResponse createPlayer(CreatePlayerRequest playerRequest) {

    playerValidator.validateRequest(playerRequest);
    Game game = gameRepository.getReferenceById(playerRequest.getGameId());
    Player savedPlayer = playerRepository.save(playerMapper.toPlayer(playerRequest, game));
    return playerMapper.toCreatePlayerResponse(savedPlayer);
  }
}
