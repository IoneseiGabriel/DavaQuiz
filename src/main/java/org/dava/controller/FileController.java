package org.dava.controller;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.dava.annotation.ValidFile;
import org.dava.dto.FileDto;
import org.dava.response.FileResponse;
import org.dava.response.FileUploadResponse;
import org.dava.security.HeaderInterceptor;
import org.dava.service.FileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * REST controller that exposes endpoints for uploading and retrieving {@link MultipartFile} resources.
 */
@RestController
@RequestMapping("/api")
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
     * @param authToken the authorization token extracted from the {@code Authorization} header
     * @param file      the uploaded file, validated using {@link ValidFile}
     * @return a {@link ResponseEntity} containing a {@link FileUploadResponse} with details about the upload result
     * @throws IOException          if the content of the file could not be read with {@link MultipartFile#getBytes()}
     */
    @PostMapping("/upload")
    public ResponseEntity<@NonNull FileUploadResponse> upload(
            @RequestHeader("Authorization") String authToken,
            @ValidFile @RequestParam("file") MultipartFile file) throws IOException {

        headerInterceptor.isAuthorizationTokenValid(authToken);
        FileUploadResponse response = fileService.upload(file);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves a file based on its name.
     * <p>
     * This endpoint receives a file name and searches for the corresponding file
     * in the database. If found, the file content is returned as a {@code byte[]},
     * with the appropriate {@code Content-Type} header so the file can be
     * rendered by clients, such as web browsers.
     * <p>
     * If no file with the given name exists, a {@link FileNotFoundException} is
     * thrown.
     * </p>
     *
     * @param authToken the authorization token extracted from the {@code Authorization} header
     * @param fileName  the name of the file to retrieve from the database
     * @return a {@link ResponseEntity} containing the file content as a {@code byte[]}
     * @throws FileNotFoundException if no file with the specified name is found
     */
    @GetMapping("/images/{fileName}")
    public ResponseEntity<byte[]> getByName(@RequestHeader("Authorization") String authToken,
                                                 @PathVariable String fileName) throws FileNotFoundException {

        headerInterceptor.isAuthorizationTokenValid(authToken);

        FileDto fileDto = fileService.getByName(fileName);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(fileDto.getType()));

        return new ResponseEntity<>(fileDto.getContent(), headers, HttpStatus.OK);
    }

    /**
     * Retrieves a list with all uploaded files.
     *
     * @param authToken the authorization token extracted from the {@code Authorization} header
     * @return a {@link ResponseEntity} containing a list of {@link FileResponse}
     */
    @GetMapping("/files")
    public ResponseEntity<@NonNull List<FileResponse>> getAll(
            @RequestHeader("Authorization") String authToken) {

        headerInterceptor.isAuthorizationTokenValid(authToken);
        List<FileResponse> response = fileService.getAll();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}