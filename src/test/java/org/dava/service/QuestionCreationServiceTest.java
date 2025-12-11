package org.dava.service;

import org.dava.dao.GameRepository;
import org.dava.domain.Game;
import org.dava.domain.GameStatus;
import org.dava.domain.Question;
import org.dava.domain.QuestionCreationRequest;
import org.dava.validator.GameValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionCreationServiceTest {

    @Mock
    private GameRepository gameRepository;

    @Mock
    private GameValidator gameValidator;

    @InjectMocks
    private QuestionCreationService questionCreationService;

    private Game draftGame;

    @BeforeEach
    void setUp() {
        draftGame = new Game();
        draftGame.setId(1L);
        draftGame.setTitle("Test game");
        draftGame.setDescription("desc");
        draftGame.setStatus(GameStatus.DRAFT);
        draftGame.setCreatedBy(200L);
        draftGame.setCreatedAt(LocalDateTime.now());
        draftGame.setUpdatedAt(LocalDateTime.now());
    }

    private QuestionCreationRequest buildValidRequest() {
        QuestionCreationRequest req = new QuestionCreationRequest();
        req.setText("What is 2+2?");
        req.setOptions(List.of("3", "4", "5", "6"));
        req.setCorrectOptionIndex(1);
        req.setImageUrl(null);
        return req;
    }

    @Test
    void createQuestionForGame_shouldCreateQuestion_whenAllValidationsPass() {

        when(gameRepository.findById(1L)).thenReturn(Optional.of(draftGame));

        QuestionCreationRequest request = buildValidRequest();


        Question result = questionCreationService.createQuestionForGame(1L, request, 200L);


        assertNotNull(result);
        assertEquals("What is 2+2?", result.getText());
        assertEquals(4, result.getOptions().size());
        assertEquals(1, result.getCorrectOptionIndex());
        assertEquals(draftGame, result.getGame());
        assertTrue(draftGame.getQuestions().contains(result));


        verify(gameRepository, times(1)).save(draftGame);


        verify(gameValidator).validateGameExists(draftGame, 1L);
        verify(gameValidator).validateGameIsDraft(draftGame);
        verify(gameValidator).validateUserIsOwner(draftGame, 200L);
    }

    @Test
    void createQuestionForGame_shouldPropagateException_whenGameDoesNotExist() {

        when(gameRepository.findById(99L)).thenReturn(Optional.empty());
        QuestionCreationRequest request = buildValidRequest();


        doThrow(new IllegalArgumentException("Game not found with id: 99"))
                .when(gameValidator)
                .validateGameExists(null, 99L);


        assertThrows(IllegalArgumentException.class,
                () -> questionCreationService.createQuestionForGame(99L, request, 200L));

        verify(gameRepository, never()).save(any());
    }

    @Test
    void createQuestionForGame_shouldPropagateException_whenGameNotDraft() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(draftGame));
        QuestionCreationRequest request = buildValidRequest();

        doThrow(new IllegalStateException("Cannot add questions to a non-draft game."))
                .when(gameValidator)
                .validateGameIsDraft(draftGame);

        assertThrows(IllegalStateException.class,
                () -> questionCreationService.createQuestionForGame(1L, request, 200L));

        verify(gameRepository, never()).save(any());
    }

    @Test
    void createQuestionForGame_shouldPropagateException_whenUserNotOwner() {
        when(gameRepository.findById(1L)).thenReturn(Optional.of(draftGame));
        QuestionCreationRequest request = buildValidRequest();

        doThrow(new SecurityException("You are not allowed to modify this game."))
                .when(gameValidator)
                .validateUserIsOwner(draftGame, 200L);

        assertThrows(SecurityException.class,
                () -> questionCreationService.createQuestionForGame(1L, request, 200L));

        verify(gameRepository, never()).save(any());
    }
}
