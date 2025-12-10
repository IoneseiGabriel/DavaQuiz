package org.dava.util;

import org.dava.exception.InvalidFileException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FileUrlGeneratorTest {

    @Test
    void generateUrlWithNullPathThrowsInvalidFileException() {
        Assertions.assertThrows(InvalidFileException.class, () -> FileUrlGenerator.generateUrl(null));
    }

    @Test
    void generateUrlWithEmptyPathThrowsInvalidFileException() {
        Assertions.assertThrows(InvalidFileException.class, () -> FileUrlGenerator.generateUrl(""));
    }

    @Test
    void generateUrlWithValidPathReturnsUrl() {
        // Arrange
        String expected = "http://localhost:8080/api/images/image.png";

        // Act
        String actual = FileUrlGenerator.generateUrl("image.png");

        // Assert
        Assertions.assertEquals(expected, actual);
    }
}