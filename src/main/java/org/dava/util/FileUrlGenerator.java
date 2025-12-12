package org.dava.util;

import lombok.experimental.UtilityClass;
import org.dava.exception.InvalidFileException;

@UtilityClass
public class FileUrlGenerator {
  private final String BASE_URL = "http://localhost:8080/api";

  /**
   * Generates a valid, stable and accessible URL for an image.
   *
   * @param path the file path
   * @return a {@code generated url} for the image that can be accessed in order to render the
   *     content by the clients, such as web browsers
   * @throws InvalidFileException if a url for the file could not be generated
   */
  public String generateUrl(String path) {
    if (path == null || path.isEmpty()) {
      throw new InvalidFileException("Invalid path provided.");
    }

    return BASE_URL + "/images/" + path;
  }
}
