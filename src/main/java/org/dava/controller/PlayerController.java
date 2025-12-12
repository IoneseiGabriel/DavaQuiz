package org.dava.controller;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.dava.dto.CreatePlayerRequest;
import org.dava.dto.CreatePlayerResponse;
import org.dava.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** REST controller responsible for handling operations related to Player resources. */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PlayerController {

  private final PlayerService playerService;

  /**
   * Authenticates a user based on the provided credentials.
   *
   * @return HTTP 200 with a login response on success, 400, 401 or 404 on error
   */
  @PostMapping("/players")
  ResponseEntity<@NonNull CreatePlayerResponse> createPlayer(
      @RequestBody CreatePlayerRequest playerRequest) {

    CreatePlayerResponse response = playerService.createPlayer(playerRequest);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
