package org.dava.exception.handler;

import org.dava.exception.ExceptionMessage;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class FileExceptionHandlerTest {

    private final FileExceptionHandler handler = new FileExceptionHandler();

    @Test
    void filesizeExceededThrowsMaxUploadSizeExceededExceptionReturns413ContentTooLarge() {
        // Arrange
        MaxUploadSizeExceededException exception =
                new MaxUploadSizeExceededException(11 * 1024 * 1024L); // 11MB

        // Act
        var response = handler.handleMaxUploadSizeExceededException(exception);

        // Assert
        assertAll("MaxUploadSizeExceededException",
                () -> assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONTENT_TOO_LARGE),
                () -> assertThat(response.getBody()).isNotNull()
        );

        ExceptionMessage body = response.getBody();
        assertTrue(body.getMessage().toString().contains("Maximum upload size"));
    }

}
