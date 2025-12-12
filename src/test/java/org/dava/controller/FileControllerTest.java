package org.dava.controller;

import static org.dava.mock.FileMockData.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import jakarta.transaction.Transactional;
import java.io.IOException;
import org.dava.dao.FileRepository;
import org.dava.davaquiz.DavaQuizApplication;
import org.dava.domain.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = DavaQuizApplication.class)
@ActiveProfiles("test")
@Transactional
class FileControllerTest {

  @Autowired private WebApplicationContext context;

  @Autowired private FileRepository fileRepository;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc = webAppContextSetup(context).build();
    fileRepository.deleteAll();
  }

  @Test
  void uploadWithExistentFileInDBReturns500InternalServerError() throws Exception {
    // Arrange
    String url = "/api/upload";
    saveFile();

    // Act & Assert
    mockMvc.perform(multipart(url).file(VALID_FILE)).andExpect(status().isInternalServerError());
  }

  @Test
  void uploadWithInvalidFileReturns400BadRequest() throws Exception {
    // Arrange
    String url = "/api/upload";
    MockMultipartFile noNameFile =
        new MockMultipartFile("file", null, "image/webp", "image".getBytes());

    // Act & Assert
    mockMvc.perform(multipart(url).file(noNameFile)).andExpect(status().isInternalServerError());
  }

  @Test
  void uploadWithFailedGeneratedUrlReturns500InternalServerError() throws Exception {
    // Arrange
    String url = "/api/upload";

    // Act & Assert
    mockMvc
        .perform(multipart(url).file(FILE_WITH_INVALID_CONTENT_TYPE))
        .andExpect(status().isBadRequest());
  }

  @Test
  void uploadWithValidFileReturnsOk200() throws Exception {
    // Arrange
    File file = buildFile(VALID_FILE);
    String url = "/api/upload";

    // Act & Assert
    mockMvc
        .perform(multipart(url).file(VALID_FILE))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.url").value(file.getUrl()));
  }

  @Test
  void getByNameWithExistentFileReturnsOK200AndFileBytes() throws Exception {
    // Arrange
    String url = "/api/images/";
    saveFile();

    // Act & Assert
    mockMvc
        .perform(get(url + VALID_FILE.getOriginalFilename()))
        .andExpect(status().isOk())
        .andExpect(header().string("Content-Type", VALID_FILE.getContentType()))
        .andExpect(content().bytes(VALID_FILE.getBytes()));
  }

  @Test
  void getByNameWithNonexistentFileReturns404NotFound() throws Exception {
    mockMvc.perform(get("/api/images/missing.png")).andExpect(status().isNotFound());
  }

  @Test
  void getAllWithOneFileReturnsOk200AndListOfOneFileResponses() throws Exception {
    // Arrange
    saveFile();

    // Act & Assert
    mockMvc
        .perform(get("/api/files"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].name").value(VALID_FILE.getOriginalFilename()));
  }

  private void saveFile() throws IOException {
    File existing = buildFile(VALID_FILE);
    fileRepository.save(existing);
  }
}
