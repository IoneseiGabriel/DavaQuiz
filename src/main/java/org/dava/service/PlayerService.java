package org.dava.service;

import org.dava.dto.CreatePlayerRequest;
import org.dava.dto.CreatePlayerResponse;

public interface PlayerService {
  CreatePlayerResponse createPlayer(CreatePlayerRequest playerRequest);
}
