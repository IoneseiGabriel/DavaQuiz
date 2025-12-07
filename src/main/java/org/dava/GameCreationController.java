package org.dava;

import org.dava.domain.GameCreationRequest;
import org.dava.domain.Game;
import org.dava.service.GameCreationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class GameCreationController {

    private final GameCreationService gameCreationService;

    public GameCreationController(GameCreationService gameCreationService) {
        this.gameCreationService = gameCreationService;
    }

    // Simple implementation for checking database updates
    @GetMapping
    public Iterable<Game> getAllGames() {
        return gameCreationService.getAllGames();
    }

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody GameCreationRequest request) {
        // User id should be that of the host - mocking it for now
        Long userId = 200L;

        Game game = gameCreationService.createGame(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(game);
    }
}

