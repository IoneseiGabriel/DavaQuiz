package org.dava.service;

import org.dava.dao.GameRepository;
import org.dava.dao.QuestionRepository;
import org.dava.domain.Game;
import org.dava.domain.GameStatus;
import org.dava.domain.Question;
import org.dava.domain.QuestionCreationRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class QuestionCreationService {

    private final GameRepository gameRepository;
    private final QuestionRepository questionRepository;

    public QuestionCreationService(GameRepository gameRepository,
                                   QuestionRepository questionRepository) {
        this.gameRepository = gameRepository;
        this.questionRepository = questionRepository;
    }

    public Question createQuestionForGame(Long gameId,
                                          QuestionCreationRequest request,
                                          Long currentUserId) {

        // 1) Căutăm game-ul
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found with id: " + gameId));

        // 2) Verificăm status == DRAFT
        if (game.getStatus() != GameStatus.DRAFT) {
            throw new IllegalStateException("Cannot add questions to a non-draft game.");
        }

        // 3) Verificăm drepturile user-ului
        if (!game.getCreatedBy().equals(currentUserId)) {
            throw new SecurityException("You are not allowed to modify this game.");
        }



        // 4) Construim noua întrebare din request
        Question question = new Question();
        question.setText(request.getText());
        question.setOptions(request.getOptions());
        question.setCorrectOptionIndex(request.getCorrectOptionIndex());
        question.setImageUrl(request.getImageUrl());

        // 5) Atașăm întrebarea la game (setGame + adăugare în listă)
        game.addQuestion(question); // metoda din Game setează și question.setGame(this);

        // 6) Updatăm updatedAt la game
        game.setUpdatedAt(LocalDateTime.now());

        // 7) Salvăm întâi game (cascade = ALL salvează și întrebarea),
        // sau salvăm direct question (pentru că are game setat).
        // Ambele variante merg; alegem să salvăm game:
        gameRepository.save(game);

        // Acum question are id generat
        return question;
    }
}
