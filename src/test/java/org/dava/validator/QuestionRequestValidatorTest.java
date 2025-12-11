package org.dava.validator;

import org.dava.domain.QuestionCreationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QuestionRequestValidatorTest {

    private QuestionRequestValidator validator;

    @BeforeEach
    void setUp() {
        validator = new QuestionRequestValidator();
    }

    private QuestionCreationRequest buildValidRequest() {
        QuestionCreationRequest req = new QuestionCreationRequest();
        req.setText("Sample question?");
        req.setOptions(List.of("A", "B", "C"));
        req.setCorrectOptionIndex(1);
        req.setImageUrl(null);
        return req;
    }

    @Test
    void validateRequestBody_shouldPass_whenRequestIsValid() {
        QuestionCreationRequest request = buildValidRequest();

        assertDoesNotThrow(() -> validator.validateRequestBody(request));
    }

    @Test
    void validateRequestBody_shouldThrow_whenRequestIsNull() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateRequestBody(null)
        );

        assertEquals("Body must not be empty", ex.getMessage());
    }

    @Test
    void validateRequestBody_shouldThrow_whenTextIsNull() {
        QuestionCreationRequest request = buildValidRequest();
        request.setText(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateRequestBody(request)
        );

        assertEquals("Question text must not be empty", ex.getMessage());
    }

    @Test
    void validateRequestBody_shouldThrow_whenTextIsBlank() {
        QuestionCreationRequest request = buildValidRequest();
        request.setText("   ");

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateRequestBody(request)
        );

        assertEquals("Question text must not be empty", ex.getMessage());
    }

    @Test
    void validateRequestBody_shouldThrow_whenOptionsNull() {
        QuestionCreationRequest request = buildValidRequest();
        request.setOptions(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateRequestBody(request)
        );

        assertEquals("Options list must not be empty", ex.getMessage());
    }

    @Test
    void validateRequestBody_shouldThrow_whenOptionsEmpty() {
        QuestionCreationRequest request = buildValidRequest();
        request.setOptions(List.of());

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateRequestBody(request)
        );

        assertEquals("Options list must not be empty", ex.getMessage());
    }

    @Test
    void validateRequestBody_shouldThrow_whenCorrectIndexNull() {
        QuestionCreationRequest request = buildValidRequest();
        request.setCorrectOptionIndex(null);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> validator.validateRequestBody(request)
        );

        assertEquals("Correct option index is required", ex.getMessage());
    }



}
