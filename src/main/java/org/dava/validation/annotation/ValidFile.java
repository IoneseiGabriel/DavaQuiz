package org.dava.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.dava.validation.FileValidator;
import org.springframework.web.multipart.MultipartFile;

/**
 * Validation annotation used to verify that a {@link MultipartFile} meets specific file-related
 * constraints.
 *
 * <p>Applying this annotation triggers the {@link FileValidator}, which performs checks such as
 * validating file type and content. It can be applied to fields, method parameters or constructor
 * parameters that require file validation.
 *
 * <p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.CONSTRUCTOR})
@Constraint(validatedBy = {FileValidator.class})
public @interface ValidFile {
  String message() default "Supported file types: JPEG, PNG, WEBP, GIF";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
