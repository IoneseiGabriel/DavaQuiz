package org.dava.controller;



import org.dava.domain.Question;
import org.dava.domain.QuestionCreationRequest;
import org.dava.service.QuestionCreationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.dava.validator.QuestionRequestValidator;

@RestController
@RequestMapping("/api/games")
public class QuestionCreationController {

    private final QuestionCreationService questionCreationService;
    private final QuestionRequestValidator questionRequestValidator;

    public QuestionCreationController(QuestionCreationService questionCreationService, QuestionRequestValidator questionRequestValidator) {
        this.questionCreationService = questionCreationService;
        this.questionRequestValidator = questionRequestValidator;
    }

    // QUESTIONS-2 — POST /api/games/{gameId}/questions
    @PostMapping("/{gameId}/questions")
    public ResponseEntity<?> createQuestionForGame(
            @PathVariable Long gameId,
            @RequestBody(required = false) QuestionCreationRequest request) {

        try {
            questionRequestValidator.validateRequestBody(request);
        } catch (IllegalArgumentException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }


        Long currentUserId = 200L;

        try {
            Question created = questionCreationService.createQuestionForGame(gameId, request, currentUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IllegalArgumentException e) {

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalStateException e) {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (SecurityException e) {

            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
