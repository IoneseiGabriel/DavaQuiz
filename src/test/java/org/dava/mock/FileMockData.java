package org.dava.mock;

import java.io.IOException;
import org.dava.domain.File;
import org.dava.dto.FileDto;
import org.dava.response.FileResponse;
import org.dava.response.FileUploadResponse;
import org.dava.util.FileUrlGenerator;
import org.junit.jupiter.api.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class FileMockData {
  public static final MockMultipartFile VALID_FILE =
      new MockMultipartFile("file", "validFile.webp", "image/webp", "image".getBytes());

  public static final MockMultipartFile FILE_WITH_INVALID_CONTENT_TYPE =
      new MockMultipartFile(
          "file", "invalidFile.txt", String.valueOf(MediaType.TEXT_PLAIN), "txt".getBytes());

  public static final MockMultipartFile EMPTY_FILE =
      new MockMultipartFile(
          "file", "invalidFile.jpeg", String.valueOf(MediaType.IMAGE_JPEG), "".getBytes());

  public static final MockMultipartFile FILE_WITH_NO_CONTENT_TYPE =
      new MockMultipartFile("file", "invalidFile.jpeg", null, "".getBytes());

  public static FileUploadResponse buildFileUploadResponse(String url) {
    return new FileUploadResponse(url);
  }

  public static File buildFile(MultipartFile file) throws IOException {
    return File.builder()
        .name(file.getOriginalFilename())
        .content(file.getBytes())
        .contentType(file.getContentType())
        .url(FileUrlGenerator.generateUrl(file.getOriginalFilename()))
        .build();
  }

  public static FileResponse buildFileResponse(File file) {
    return FileResponse.builder()
        .id(file.getId())
        .name(file.getName())
        .contentType(file.getContentType())
        .url(file.getUrl())
        .build();
  }

  public static FileDto buildFileDto(File file) {
    return new FileDto(file.getContentType(), file.getContent());
  }
}
