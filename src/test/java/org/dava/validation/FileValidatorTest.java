package org.dava.validation;

import jakarta.validation.ConstraintValidatorContext;
import org.dava.mock.FileMockData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.dava.validation.FileValidationMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileValidatorTest {

    @Mock
    ConstraintValidatorContext context;

    @Mock
    ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

    FileValidator fileValidator;

    @BeforeEach
    void setup() {
        fileValidator = new FileValidator();
    }

    @Test
    void isValidWithNullFileReturnsFalse() {
        // Act
        setupValidationContextMocks();
        boolean result = fileValidator.isValid(null, context);

        // Assert
        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(INVALID_FILE_MESSAGE.getMessage());
    }

    @Test
    void isValidWithEmptyFileReturnsFalse() {
        // Arrange
        setupValidationContextMocks();
        MockMultipartFile invalidFile = FileMockData.EMPTY_FILE;

        // Act
        boolean result = fileValidator.isValid(invalidFile, context);

        // Assert
        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(EMPTY_FILE_MESSAGE.getMessage());
    }

    @Test
    void isValidWithInvalidFileTypeReturnsFalse() {
        // Arrange
        setupValidationContextMocks();
        MockMultipartFile invalidFile = FileMockData.FILE_WITH_INVALID_CONTENT_TYPE;

        // Act
        boolean result = fileValidator.isValid(invalidFile, context);

        // Assert
        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(
                INVALID_FILE_TYPE_MESSAGE.getMessage() + ": " + invalidFile.getContentType());
    }

    @Test
    void isValidWithNullFileTypeReturnsFalse() {
        // Arrange
        setupValidationContextMocks();
        MockMultipartFile invalidFile = FileMockData.FILE_WITH_NO_CONTENT_TYPE;

        // Act
        boolean result = fileValidator.isValid(invalidFile, context);

        // Assert
        assertFalse(result);
        verify(context).disableDefaultConstraintViolation();
        verify(context).buildConstraintViolationWithTemplate(FILE_TYPE_NOT_SPECIFIED_MESSAGE.getMessage());
    }

    @Test
    void isValidWithValidFileReturnsTrue() {
        // Arrange
        MockMultipartFile validFile = FileMockData.VALID_FILE;

        // Act
        boolean result = fileValidator.isValid(validFile, context);

        // Assert
        assertTrue(result);
    }

    private void setupValidationContextMocks() {
        when(context.buildConstraintViolationWithTemplate(anyString()))
                .thenReturn(violationBuilder);

        when(violationBuilder.addConstraintViolation())
                .thenReturn(context);
    }
}