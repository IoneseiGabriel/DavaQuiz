package org.dava.controller;

import org.dava.domain.Question;
import org.dava.domain.QuestionCreationRequest;
import org.dava.service.QuestionCreationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class QuestionCreationControllerTest {

    @Mock
    private QuestionCreationService questionCreationService;

    @InjectMocks
    private QuestionCreationController questionCreationController;

    private QuestionCreationRequest validRequest;

    @BeforeEach
    void setUp() {
        validRequest = new QuestionCreationRequest();
        validRequest.setText("What is the capital of France?");
        validRequest.setOptions(List.of("Paris", "London", "Berlin", "Rome"));
        validRequest.setCorrectOptionIndex(0);
        validRequest.setImageUrl(null);
    }

    // 201 CREATED – când totul e ok
    @Test
    void createQuestionForGame_shouldReturn201_whenRequestIsValid() {
        // arrange
        Question created = new Question();
        created.setId(10L);
        created.setText(validRequest.getText());
        created.setOptions(validRequest.getOptions());
        created.setCorrectOptionIndex(validRequest.getCorrectOptionIndex());

        // service-ul întoarce întrebarea creată
        when(questionCreationService.createQuestionForGame(
                eq(1L), any(QuestionCreationRequest.class), eq(200L))
        ).thenReturn(created);

        // act
        ResponseEntity<?> response =
                questionCreationController.createQuestionForGame(1L, validRequest);

        // assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Question);

        Question body = (Question) response.getBody();
        assertEquals(10L, body.getId());
        assertEquals("What is the capital of France?", body.getText());
    }

    // 400 BAD REQUEST – body null (request lipsă)
    @Test
    void createQuestionForGame_shouldReturn400_whenBodyIsNull() {
        ResponseEntity<?> response =
                questionCreationController.createQuestionForGame(1L, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Body must not be empty", response.getBody());
    }

    // 400 BAD REQUEST – game nu este DRAFT (service aruncă IllegalStateException)
    @Test
    void createQuestionForGame_shouldReturn400_whenGameNotDraft() {
        doThrow(new IllegalStateException("Cannot add questions to a non-draft game."))
                .when(questionCreationService)
                .createQuestionForGame(eq(1L), any(QuestionCreationRequest.class), eq(200L));

        ResponseEntity<?> response =
                questionCreationController.createQuestionForGame(1L, validRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot add questions to a non-draft game.", response.getBody());
    }

    // 403 FORBIDDEN – user-ul 200L nu este owner (service aruncă SecurityException)
    @Test
    void createQuestionForGame_shouldReturn403_whenUserNotOwner() {
        doThrow(new SecurityException("You are not allowed to modify this game."))
                .when(questionCreationService)
                .createQuestionForGame(eq(1L), any(QuestionCreationRequest.class), eq(200L));

        ResponseEntity<?> response =
                questionCreationController.createQuestionForGame(1L, validRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("You are not allowed to modify this game.", response.getBody());
    }

    // 404 NOT FOUND – game not found (service aruncă IllegalArgumentException)
    @Test
    void createQuestionForGame_shouldReturn404_whenGameNotFound() {
        doThrow(new IllegalArgumentException("Game not found with id: 99"))
                .when(questionCreationService)
                .createQuestionForGame(eq(99L), any(QuestionCreationRequest.class), eq(200L));

        ResponseEntity<?> response =
                questionCreationController.createQuestionForGame(99L, validRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Game not found with id: 99", response.getBody());
    }
}
