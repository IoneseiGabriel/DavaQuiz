package org.dava.validator;

import static org.dava.validator.FileValidationMessages.*;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.dava.dao.FileRepository;
import org.dava.domain.File;
import org.dava.exception.ExistentFileException;
import org.dava.validator.annotation.ValidFile;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * Validates a {@link MultipartFile} based on the rules defined by the {@link ValidFile} annotation.
 *
 * <p>This validator checks whether an uploaded file meets specific type and content criteria,
 * depending on the logic implemented in the {@code isValid} method.
 */
@Component
@RequiredArgsConstructor
public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
  private final FileRepository fileRepository;

  private static final Set<MediaType> SUPPORTED_FILE_TYPES =
      Set.of(MediaType.IMAGE_JPEG, MediaType.IMAGE_GIF, MediaType.IMAGE_PNG);

  /**
   * Initializes the validator by receiving the annotation instance applied to the field or
   * parameter being validated.
   *
   * <p>This method can be used to read annotation attributes, although the current implementation
   * does not require any custom setup.
   *
   * @param constraintAnnotation the {@link ValidFile} annotation instance from which validation
   *     parameters may be extracted
   */
  @Override
  public void initialize(ValidFile constraintAnnotation) {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  /**
   * Performs validation of the provided {@link MultipartFile} parameter.
   *
   * <p>This method checks whether the file satisfies the validation criteria, such as checking
   * whether the file is not null, not empty and the content type is supported. If the file does not
   * meet the requirements, a custom constraint violation message is set to the {@link
   * ConstraintValidatorContext}, using {@link
   * FileValidator#setContextWithException(ConstraintValidatorContext, String)}, and the method
   * returns {@code false}.
   *
   * @param file the file to be validated
   * @param context the context used to build custom validation error messages
   * @return {@code true} if the file is valid; {@code false} otherwise
   */
  @Override
  public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
    if (isFileNull(file, context)) return false;
    if (!isFileTypeValid(file, context)) return false;
    return !isFileEmpty(file, context);
  }

  public void checkIfFileExists(String fileName) {
    Optional<File> file = fileRepository.findByName(fileName);
    if (file.isPresent()) {
      throw new ExistentFileException(
          "A file with the same name was already uploaded: " + fileName);
    }
  }

  private static boolean isFileTypeValid(MultipartFile file, ConstraintValidatorContext context) {
    String fileType = file.getContentType();
    if (fileType == null) {
      setContextWithException(context, FILE_TYPE_NOT_SPECIFIED_MESSAGE.getMessage());
      return false;
    }

    boolean isValidType =
        SUPPORTED_FILE_TYPES.contains(MediaType.parseMediaType(fileType))
            || fileType.equals("image/webp");
    if (!isValidType) {
      setContextWithException(context, INVALID_FILE_TYPE_MESSAGE.getMessage() + ": " + fileType);
      return false;
    }

    return true;
  }

  private static boolean isFileEmpty(MultipartFile file, ConstraintValidatorContext context) {
    boolean isFileEmpty = file.isEmpty();

    if (isFileEmpty) {
      setContextWithException(context, EMPTY_FILE_MESSAGE.getMessage());
    }

    return isFileEmpty;
  }

  private static boolean isFileNull(MultipartFile file, ConstraintValidatorContext context) {
    if (file == null) {
      setContextWithException(context, INVALID_FILE_MESSAGE.getMessage());
      return true;
    }

    return false;
  }

  private static void setContextWithException(ConstraintValidatorContext context, String message) {
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
  }
}
