package org.dava.controller;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.dava.response.FileUploadResponse;
import org.dava.security.HeaderInterceptor;
import org.dava.service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
@AllArgsConstructor
@Slf4j
public class FileController {
    private final FileService fileService;
    private final HeaderInterceptor headerInterceptor;


    @PostMapping
    public ResponseEntity<@NonNull FileUploadResponse> upload(
            @RequestHeader("Authorization") String authToken,
            @RequestParam("file") MultipartFile file) {

        if (headerInterceptor.isAuthorizationTokenValid(authToken)) {
            FileUploadResponse response = fileService.upload(file);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().build();
    }
}
