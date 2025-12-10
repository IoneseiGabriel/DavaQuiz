package org.dava.service;

import org.dava.response.FileUploadResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    FileUploadResponse upload(MultipartFile file);
}
