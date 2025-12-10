package org.dava.util;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.dava.annotation.ValidFile;
import org.dava.dto.FileValidationDto;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * Validates a {@link MultipartFile} based on the rules defined by the {@link ValidFile} annotation.
 * <p>
 * This validator checks whether an uploaded file meets specific type and content criteria,
 * depending on the logic implemented in the {@code isValid} method.
 * </p>
 */
public class FileValidator implements ConstraintValidator<ValidFile, MultipartFile> {
    private static final Set<MimeType> SUPPORTED_FILE_TYPES = Set.of(MimeTypeUtils.IMAGE_JPEG, MimeTypeUtils.IMAGE_GIF, MimeTypeUtils.IMAGE_PNG);

    /**
     * Initializes the validator by receiving the annotation instance applied
     * to the field or parameter being validated.
     * <p>
     * This method can be used to read annotation attributes, although the
     * current implementation does not require any custom setup.
     *
     * @param constraintAnnotation the {@link ValidFile} annotation instance
     *                             from which validation parameters may be extracted
     */
    @Override
    public void initialize(ValidFile constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Performs validation of the provided {@link MultipartFile} parameter.
     * <p>
     * This method checks whether the file satisfies the validation criteria
     * defined in {@link FileValidator#checkFileType(MultipartFile)}
     * and {@link FileValidator#checkFileContent(MultipartFile)}.
     * If the file does not meet the requirements, a custom constraint violation message is set to the
     * {@link ConstraintValidatorContext}, using {@link FileValidator#setContextWithException(ConstraintValidatorContext, String)},
     * and the method returns {@code false}.
     *
     * @param file    the file to be validated
     * @param context the context used to build custom validation error messages
     * @return {@code true} if the file is valid; {@code false} otherwise
     */
    @Override
    public boolean isValid(MultipartFile file, ConstraintValidatorContext context) {
        FileValidationDto isFileValid = checkFileType(file);
        if (!isFileValid.isValid()) {
            setContextWithException(context, isFileValid.getMessage());
            return false;
        }

        isFileValid = checkFileContent(file);
        if (!isFileValid.isValid()) {
            setContextWithException(context, isFileValid.getMessage());
            return false;
        }

        return true;
    }

    private static FileValidationDto checkFileType(MultipartFile file) {
        String fileType = file.getContentType();
        if (fileType == null) {
            return new FileValidationDto("File type is not specified.", false);
        }

        boolean isValidType = SUPPORTED_FILE_TYPES.contains(MimeTypeUtils.parseMimeType(fileType)) || fileType.equals("image/webp");
        if (!isValidType) {
            return new FileValidationDto(String.format("Invalid file type: %s", fileType), false);
        }

        return new FileValidationDto(null, true);
    }

    private static FileValidationDto checkFileContent(MultipartFile file) {
        boolean isFileEmpty = file.isEmpty();
        String message = isFileEmpty ? "File is empty." : null;
        return new FileValidationDto(message, !isFileEmpty);
    }

    private static void setContextWithException(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}