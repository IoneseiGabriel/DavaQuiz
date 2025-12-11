package org.dava.validator;

import org.dava.domain.Game;
import org.dava.domain.GameStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameValidatorTest {

    private GameValidator gameValidator;

    @BeforeEach
    void setUp() {
        gameValidator = new GameValidator();
    }

    private Game buildDraftGameOwnedBy(long ownerId) {
        Game game = new Game();
        game.setId(1L);
        game.setTitle("Test game");
        game.setStatus(GameStatus.DRAFT);
        game.setCreatedBy(ownerId);
        return game;
    }

    @Test
    void validateGameExists_shouldPass_whenGameNotNull() {
        Game game = buildDraftGameOwnedBy(200L);

        assertDoesNotThrow(() -> gameValidator.validateGameExists(game, 1L));
    }

    @Test
    void validateGameExists_shouldThrow_whenGameIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> gameValidator.validateGameExists(null, 99L)
        );

        assertEquals("Game not found with id: 99", ex.getMessage());
    }

    @Test
    void validateGameIsDraft_shouldPass_whenGameIsDraft() {
        Game game = buildDraftGameOwnedBy(200L);

        assertDoesNotThrow(() -> gameValidator.validateGameIsDraft(game));
    }

    @Test
    void validateGameIsDraft_shouldThrow_whenGameIsNotDraft() {
        Game game = buildDraftGameOwnedBy(200L);
        game.setStatus(GameStatus.PUBLISHED);

        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> gameValidator.validateGameIsDraft(game)
        );

        assertEquals("Cannot add questions to a non-draft game.", ex.getMessage());
    }

    @Test
    void validateUserIsOwner_shouldPass_whenUserIsOwner() {
        Game game = buildDraftGameOwnedBy(200L);

        assertDoesNotThrow(() -> gameValidator.validateUserIsOwner(game, 200L));
    }

    @Test
    void validateUserIsOwner_shouldThrow_whenUserIsNotOwner() {
        Game game = buildDraftGameOwnedBy(200L);

        SecurityException ex = assertThrows(
                SecurityException.class,
                () -> gameValidator.validateUserIsOwner(game, 300L)
        );

        assertEquals("You are not allowed to modify this game.", ex.getMessage());
    }
}
