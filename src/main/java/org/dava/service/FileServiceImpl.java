package org.dava.service;

import org.dava.response.FileUploadResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileServiceImpl implements FileService {

    public FileUploadResponse upload(MultipartFile file) {
        return new FileUploadResponse(file.getOriginalFilename());
    }
}
