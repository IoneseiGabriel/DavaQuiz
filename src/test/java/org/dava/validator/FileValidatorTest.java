package org.dava.validator;

import static org.dava.mock.FileMockData.VALID_FILE;
import static org.dava.mock.FileMockData.buildFile;
import static org.dava.validator.FileValidationMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import jakarta.validation.ConstraintValidatorContext;
import java.io.IOException;
import java.util.Optional;
import org.dava.dao.FileRepository;
import org.dava.domain.File;
import org.dava.exception.ExistentFileException;
import org.dava.mock.FileMockData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

@ExtendWith(MockitoExtension.class)
class FileValidatorTest {

  @Mock ConstraintValidatorContext context;

  @Mock ConstraintValidatorContext.ConstraintViolationBuilder violationBuilder;

  @Mock FileRepository fileRepository;

  @InjectMocks FileValidator fileValidator;

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
    verify(context)
        .buildConstraintViolationWithTemplate(
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
    verify(context)
        .buildConstraintViolationWithTemplate(FILE_TYPE_NOT_SPECIFIED_MESSAGE.getMessage());
  }

  @Test
  void isValidWithValidFileReturnsTrue() {
    // Act
    boolean result = fileValidator.isValid(FileMockData.VALID_FILE, context);

    // Assert
    assertTrue(result);
  }

  @Test
  void checkIfFileExistsWithExistentFileThrowsExistentFileException() throws IOException {
    // Arrange
    File file = buildFile(VALID_FILE);
    when(fileRepository.findByName(anyString())).thenReturn(Optional.of(file));

    // Act & Assert
    assertThrows(ExistentFileException.class, () -> fileValidator.checkIfFileExists("fileName"));
  }

  private void setupValidationContextMocks() {
    when(context.buildConstraintViolationWithTemplate(anyString())).thenReturn(violationBuilder);

    when(violationBuilder.addConstraintViolation()).thenReturn(context);
  }
}
