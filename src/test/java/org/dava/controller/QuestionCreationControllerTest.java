package org.dava.controller;

import org.dava.domain.Question;
import org.dava.domain.QuestionCreationRequest;
import org.dava.service.QuestionCreationService;
import org.dava.validator.QuestionRequestValidator;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class QuestionCreationControllerTest {

    @Mock
    private QuestionCreationService questionCreationService;

    @Mock
    private QuestionRequestValidator questionRequestValidator;

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


    @Test
    void createQuestionForGame_shouldReturn201_whenRequestIsValid() {


        doNothing().when(questionRequestValidator).validateRequestBody(any());

        Question created = new Question();
        created.setId(10L);
        created.setText(validRequest.getText());
        created.setOptions(validRequest.getOptions());
        created.setCorrectOptionIndex(validRequest.getCorrectOptionIndex());

        when(questionCreationService.createQuestionForGame(
                eq(1L), any(QuestionCreationRequest.class), eq(200L))
        ).thenReturn(created);

        ResponseEntity<?> response =
                questionCreationController.createQuestionForGame(1L, validRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(response.getBody() instanceof Question);

        Question body = (Question) response.getBody();
        assertEquals(10L, body.getId());
        assertEquals("What is the capital of France?", body.getText());
    }


    @Test
    void createQuestionForGame_shouldReturn400_whenBodyIsNull() {

        doThrow(new IllegalArgumentException("Body must not be empty"))
                .when(questionRequestValidator).validateRequestBody(null);

        ResponseEntity<?> response =
                questionCreationController.createQuestionForGame(1L, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Body must not be empty", response.getBody());
    }


    @Test
    void createQuestionForGame_shouldReturn400_whenGameNotDraft() {

        doNothing().when(questionRequestValidator).validateRequestBody(any());

        doThrow(new IllegalStateException("Cannot add questions to a non-draft game."))
                .when(questionCreationService)
                .createQuestionForGame(eq(1L), any(), eq(200L));

        ResponseEntity<?> response =
                questionCreationController.createQuestionForGame(1L, validRequest);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Cannot add questions to a non-draft game.", response.getBody());
    }


    @Test
    void createQuestionForGame_shouldReturn403_whenUserNotOwner() {

        doNothing().when(questionRequestValidator).validateRequestBody(any());

        doThrow(new SecurityException("You are not allowed to modify this game."))
                .when(questionCreationService)
                .createQuestionForGame(eq(1L), any(), eq(200L));

        ResponseEntity<?> response =
                questionCreationController.createQuestionForGame(1L, validRequest);

        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals("You are not allowed to modify this game.", response.getBody());
    }

    @Test
    void createQuestionForGame_shouldReturn404_whenGameNotFound() {

        doNothing().when(questionRequestValidator).validateRequestBody(any());

        doThrow(new IllegalArgumentException("Game not found with id: 99"))
                .when(questionCreationService)
                .createQuestionForGame(eq(99L), any(), eq(200L));

        ResponseEntity<?> response =
                questionCreationController.createQuestionForGame(99L, validRequest);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Game not found with id: 99", response.getBody());
    }
}
