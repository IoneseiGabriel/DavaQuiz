package org.dava.validator;


import org.dava.domain.QuestionCreationRequest;
import org.springframework.stereotype.Component;


@Component
public class QuestionRequestValidator {

    public void validateRequestBody(QuestionCreationRequest request)
    {
        if(request == null) {
            throw new IllegalArgumentException("Body must not be empty");
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
    }

}
