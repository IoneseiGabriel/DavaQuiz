package org.dava.service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import org.dava.dto.FileDto;
import org.dava.response.FileResponse;
import org.dava.response.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
  FileUploadResponse upload(MultipartFile file) throws IOException;

  List<FileResponse> getAll();

  FileDto getByName(String name) throws FileNotFoundException;
}
