package org.dava.dto;

import lombok.*;

/**
 * Represents the result of a condition for file validation.
 * <p>
 * This DTO indicates if a file passed a certain validation rule or not.
 * It includes a boolean {@code isValid} field, representing whether the condition was fulfilled or not
 * and a String {@code message} field that represents a descriptive message of the outcome.
 * </p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileValidationDto {
    private String message;
    private boolean isValid;
}
