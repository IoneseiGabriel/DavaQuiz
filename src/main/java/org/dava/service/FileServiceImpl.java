package org.dava.service;

import lombok.AllArgsConstructor;
import org.dava.dao.FileRepository;
import org.dava.domain.File;
import org.dava.dto.FileDto;
import org.dava.mapper.FileMapper;
import org.dava.response.FileResponse;
import org.dava.response.FileUploadResponse;
import org.dava.util.FileUrlGenerator;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * An implementation for the {@link FileService} interface.
 * Provides methods for retrieving & uploading files.
 */
@Service
@AllArgsConstructor
public class FileServiceImpl implements FileService {
    private final FileRepository fileRepository;
    private final FileMapper fileMapper;

    /**
     * Uploads a {@link MultipartFile} object.
     * <p>
     * Gets a generated url for the file, using {@link FileUrlGenerator}.
     * Creates a {@link File} object containing file details, such as the name, content, content type and
     * the generated url and stores the file within the database.
     * If the file storage is successful, it returns a {@link FileUploadResponse} object,
     * representing the {@code file URL}, otherwise a descriptive exception is thrown.
     * </p>
     *
     * @param fileToUpload a {@link MultipartFile} to be uploaded
     * @return a {@link FileUploadResponse} object representing the {@code file URL}
     * @throws IOException if the content of the file could not be read with {@link MultipartFile#getBytes()}
     */
    public FileUploadResponse upload(MultipartFile fileToUpload) throws IOException {
        String url = FileUrlGenerator.generateUrl(fileToUpload.getOriginalFilename());

        File file = File.builder()
                .name(fileToUpload.getOriginalFilename())
                .content(fileToUpload.getBytes())
                .contentType(fileToUpload.getContentType())
                .url(url)
                .build();
        fileRepository.save(file);

        return new FileUploadResponse(file.getUrl());
    }

    /**
     * Retrieves a list of uploaded files.
     * <p>
     * The list of the {@link File} objects found is converted into a list of
     * {@link FileResponse} objects using the {@link FileMapper}.
     * </p>
     *
     * @return a list of {@link FileResponse}
     */
    @Override
    public List<FileResponse> getAll() {
        List<File> files = fileRepository.findAll();
        return fileMapper.toFileResponseList(files);
    }

    /**
     * Retrieves a file representation based on the provided file name.
     *
     * @param name the name of the file to be found
     * @return a {@link FileDto} object consisting of the file content and the content type
     * @throws FileNotFoundException if no file with such name was found
     */
    @Override
    public FileDto getByName(String name) throws FileNotFoundException {
        File file = fileRepository.findByName(name)
                .orElseThrow(() -> new FileNotFoundException("File with name '" + name + "' not found."));

        return new FileDto(file.getContentType(), file.getContent());
    }
}