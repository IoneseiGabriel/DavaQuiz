package org.dava.service;

import org.dava.controller.GameCreationController;
import org.dava.dao.GameRepository;
import org.dava.domain.*;
import org.dava.exception.InvalidGameException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class GameCreationService {
    private final GameRepository gameRepository;

    public GameCreationService(GameRepository gameRepository) {

        this.gameRepository = gameRepository;
    }


    public Game createGame(GameCreationRequest request, Long userId) {

        // Check if request is valid
        processGameRequest(request);

        Game newGame = new Game();

        // Set the fields for the new object with values from the request
        setGameFields(newGame, request, userId);

        // Map questions to QuestionEntity
        for (QuestionCreationRequest qReq : request.getQuestions()) {
            processQuestionFromRequest(qReq);

            Question q = new Question();

            setQuestionFields(q, qReq);

            newGame.addQuestion(q);
        }

        return gameRepository.save(newGame);
    }

    public Iterable<Game> getAllGames() {

        return gameRepository.findAll();
    }


    /* Helper Functions */

    public void processGameRequest(GameCreationRequest request) {
        if (request == null) {
            throw new InvalidGameException("Game request cannot be null");
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new InvalidGameException("Game title cannot be empty");
        }

        if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
            throw new InvalidGameException("At least one question is required");
        }
    }


    public void setGameFields(Game game, GameCreationRequest request, Long userId) {
        game.setTitle(request.getTitle());
        game.setDescription(request.getDescription());
        game.setStatus((request.getStatus() == null) ? GameStatus.DRAFT : GameStatus.PUBLISHED);
        game.setCreatedBy(userId);
        game.setCreatedAt(LocalDateTime.now());
        game.setUpdatedAt(LocalDateTime.now());
    }


    public void processQuestionFromRequest(QuestionCreationRequest q) {
        if (q.getText() == null || q.getText().isBlank()) {
            throw new InvalidGameException("Question text cannot be empty");
        }

        if (q.getOptions() == null || q.getOptions().size() < 2) {
            throw new InvalidGameException("Each question must have at least two options");
        }

        if (q.getCorrectOptionIndex() == null) {
            throw new InvalidGameException("A correct option index is required for each question");
        }

        if (q.getCorrectOptionIndex() < 0 ||
                q.getCorrectOptionIndex() >= q.getOptions().size()) {
            throw new InvalidGameException("The correct option index is out of bounds for your question: " + q.getText());
        }

        if (q.getImageUrl() == null || q.getImageUrl().isBlank()) {
            throw new InvalidGameException("Image URL cannot be empty");
        }
    }


    public void setQuestionFields(Question q, QuestionCreationRequest qReq) {
        q.setText(qReq.getText());
        q.setOptions(qReq.getOptions());
        q.setCorrectOptionIndex(qReq.getCorrectOptionIndex());
        q.setImageUrl(qReq.getImageUrl());
    }
}
