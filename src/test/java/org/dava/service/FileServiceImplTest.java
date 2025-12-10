package org.dava.service;

import org.dava.dao.FileRepository;
import org.dava.domain.File;
import org.dava.dto.FileDto;
import org.dava.mapper.FileMapper;
import org.dava.mock.FileMockData;
import org.dava.response.FileResponse;
import org.dava.response.FileUploadResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.dava.mock.FileMockData.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FileServiceImplTest {
    @Mock
    private FileRepository fileRepository;

    @Mock
    private FileMapper fileMapper;

    @InjectMocks
    private FileServiceImpl fileService;

    @Test
    void uploadWithValidFileReturnsFileUploadResponse() throws IOException {
        // Arrange
        File file = buildFile(VALID_FILE);
        when(fileRepository.save(any(File.class))).thenReturn(file);
        FileUploadResponse expected = buildFileUploadResponse(file.getUrl());

        // Act
        FileUploadResponse actual = fileService.upload(VALID_FILE);

        // Assert
        Assertions.assertEquals(expected, actual);
        verify(fileRepository).save(any(File.class));
    }

    @Test
    void getAllWithEmptyFileListReturnsEmptyFileResponseList() {
        // Arrange
        when(fileRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<FileResponse> actual = fileService.getAll();

        // Assert
        Assertions.assertEquals(Collections.emptyList(), actual);
        verify(fileRepository).findAll();
    }

    @Test
    void getAllWithFileListReturnsFileResponseList() throws IOException {
        // Arrange
        List<File> fileList = List.of(buildFile(VALID_FILE), buildFile(FILE_WITH_INVALID_CONTENT_TYPE));
        List<FileResponse> expected = fileList.stream().map(FileMockData::buildFileResponse).toList();

        when(fileRepository.findAll()).thenReturn(fileList);
        when(fileMapper.toFileResponseList(any())).thenReturn(expected);

        // Act
        List<FileResponse> actual = fileService.getAll();

        // Assert
        Assertions.assertEquals(expected, actual);
        verify(fileRepository).findAll();
        verify(fileMapper).toFileResponseList(any());
    }

    @Test
    void getByNameWithNoFileFoundThrowsFileNotFoundException() {
        // Arrange
        when(fileRepository.findByName(any())).thenReturn(Optional.empty());
        String fileName = "not found";

        // Act & Assert
        assertThrows(FileNotFoundException.class, () -> fileService.getByName(fileName));
        verify(fileRepository).findByName(fileName);
    }

    @Test
    void getByNameWithExistingFileReturnsFileDto() throws IOException {
        // Arrange
        File file = buildFile(VALID_FILE);
        when(fileRepository.findByName(any())).thenReturn(Optional.of(file));
        FileDto expected = buildFileDto(file);

        // Act
        FileDto actual = fileService.getByName(VALID_FILE.getName());

        // Assert
        Assertions.assertEquals(expected, actual);
        verify(fileRepository).findByName(VALID_FILE.getName());

    }
}