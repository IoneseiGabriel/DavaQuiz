package org.dava.service;

import org.dava.dao.GameRepository;
import org.dava.domain.Game;
import org.dava.domain.Question;
import org.dava.domain.QuestionCreationRequest;
import org.dava.validator.GameValidator;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class QuestionCreationService {

    private final GameRepository gameRepository;
    private final GameValidator gameValidator;

    public QuestionCreationService(GameRepository gameRepository,
                                   GameValidator gameValidator) {
        this.gameRepository = gameRepository;
        this.gameValidator = gameValidator;
    }

    public Question createQuestionForGame(Long gameId,
                                          QuestionCreationRequest request,
                                          Long currentUserId) {


        Game game = gameRepository.findById(gameId).orElse(null);


        gameValidator.validateGameExists(game, gameId);
        gameValidator.validateGameIsDraft(game);
        gameValidator.validateUserIsOwner(game, currentUserId);


        Question question = new Question();
        question.setText(request.getText());
        question.setOptions(request.getOptions());
        question.setCorrectOptionIndex(request.getCorrectOptionIndex());
        question.setImageUrl(request.getImageUrl());


        game.addQuestion(question);
        game.setUpdatedAt(LocalDateTime.now());


        gameRepository.save(game);

        return question;
    }
}
