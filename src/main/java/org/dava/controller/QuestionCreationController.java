package org.dava.controller;



import org.dava.domain.Question;
import org.dava.domain.QuestionCreationRequest;
import org.dava.service.QuestionCreationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/games")
public class QuestionCreationController {

    private final QuestionCreationService questionCreationService;

    public QuestionCreationController(QuestionCreationService questionCreationService) {
        this.questionCreationService = questionCreationService;
    }

    // QUESTIONS-2 — POST /api/games/{gameId}/questions
    @PostMapping("/{gameId}/questions")
    public ResponseEntity<?> createQuestionForGame(
            @PathVariable Long gameId,
            @RequestBody(required = false) QuestionCreationRequest request) {

        if (request == null){
            return ResponseEntity.badRequest().body("Body must not be empty");
        }

        if (request.getText() == null || request.getText().isBlank()) {
            throw new IllegalArgumentException("Question text must not be empty");
        }

        if (request.getOptions() == null || request.getOptions().isEmpty()) {
            throw new IllegalArgumentException("Options list must not be empty");
        }

        if (request.getCorrectOptionIndex() == null) {
            throw new IllegalArgumentException("Correct option index is required");
        }

        // Înlocuiești cu user-ul curent când aveți autentificare
        Long currentUserId = 200L;

        try {
            Question created = questionCreationService.createQuestionForGame(gameId, request, currentUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (IllegalArgumentException e) {
            // Game not found
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (IllegalStateException e) {
            // Status invalid (nu e DRAFT)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());

        } catch (SecurityException e) {
            // User nu are drepturi pe joc
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
