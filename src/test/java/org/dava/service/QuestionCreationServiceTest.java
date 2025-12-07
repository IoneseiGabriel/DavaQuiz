package org.dava.service;

import org.dava.dao.GameRepository;
import org.dava.dao.QuestionRepository;
import org.dava.domain.Game;
import org.dava.domain.GameStatus;
import org.dava.domain.Question;
import org.dava.domain.QuestionCreationRequest;
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
    private QuestionRepository questionRepository; // nu îl folosim direct, dar îl cere service-ul

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
    void createQuestionForGame_shouldCreateQuestion_whenGameIsDraftAndUserIsOwner() {
        // arrange
        when(gameRepository.findById(1L)).thenReturn(Optional.of(draftGame));

        QuestionCreationRequest request = buildValidRequest();

        // act
        Question result = questionCreationService.createQuestionForGame(1L, request, 200L);

        // assert
        assertNotNull(result);
        assertEquals("What is 2+2?", result.getText());
        assertEquals(4, result.getOptions().size());
        assertEquals(1, result.getCorrectOptionIndex());
        assertEquals(draftGame, result.getGame()); // presupune că addQuestion setează game

        // Game ar trebui să aibă întrebarea în listă
        assertTrue(draftGame.getQuestions().contains(result));

        // save(...) trebuie apelat o dată
        verify(gameRepository, times(1)).save(draftGame);
    }

    @Test
    void createQuestionForGame_shouldThrowIllegalArgument_whenGameNotFound() {
        when(gameRepository.findById(99L)).thenReturn(Optional.empty());

        QuestionCreationRequest request = buildValidRequest();

        assertThrows(IllegalArgumentException.class,
                () -> questionCreationService.createQuestionForGame(99L, request, 200L));

        verify(gameRepository, never()).save(any());
    }

    @Test
    void createQuestionForGame_shouldThrowIllegalState_whenGameNotDraft() {
        draftGame.setStatus(GameStatus.PUBLISHED);
        when(gameRepository.findById(1L)).thenReturn(Optional.of(draftGame));

        QuestionCreationRequest request = buildValidRequest();

        assertThrows(IllegalStateException.class,
                () -> questionCreationService.createQuestionForGame(1L, request, 200L));

        verify(gameRepository, never()).save(any());
    }

    @Test
    void createQuestionForGame_shouldThrowSecurityException_whenUserNotOwner() {
        draftGame.setCreatedBy(999L); // alt user
        when(gameRepository.findById(1L)).thenReturn(Optional.of(draftGame));

        QuestionCreationRequest request = buildValidRequest();

        assertThrows(SecurityException.class,
                () -> questionCreationService.createQuestionForGame(1L, request, 200L));

        verify(gameRepository, never()).save(any());
    }
}
