package org.dava.controller;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.dava.annotation.ValidFile;
import org.dava.response.FileUploadResponse;
import org.dava.security.HeaderInterceptor;
import org.dava.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * REST controller that exposes an endpoint for uploading {@link MultipartFile} resources.
 */
@RestController
@RequestMapping("/api/upload")
@AllArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;
    private final HeaderInterceptor headerInterceptor;

    /**
     * Handles a file upload request.
     * <p>
     * This endpoint accepts a file provided as multipart/form-data and applies validation rules defined
     * by the {@link ValidFile} annotation. If the file passes validation, it is processed and a response describing
     * the upload result is returned.
     * </p>
     *
     * @param authToken the authorization token extracted from the Authorization request header
     * @param file the uploaded file, validated using {@link ValidFile}
     * @return a {@link ResponseEntity} containing a {@link FileUploadResponse} with details about the upload result
     */
    @PostMapping
    public ResponseEntity<@NonNull FileUploadResponse> upload(
            @RequestHeader("Authorization") String authToken,
            @ValidFile @RequestParam("file") MultipartFile file) {

        if (headerInterceptor.isAuthorizationTokenValid(authToken)) {
            FileUploadResponse response = fileService.upload(file);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().build();
    }
}
