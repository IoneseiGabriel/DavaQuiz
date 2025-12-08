package org.dava.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.dava.davaquiz.DavaQuizApplication;
import org.dava.dao.UserRepository;
import org.dava.domain.UserEntity;
import org.dava.dto.LoginRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@SpringBootTest(classes = DavaQuizApplication.class)
@ActiveProfiles("test")
class AuthControllerIT {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = webAppContextSetup(context).build();

        userRepository.deleteAll();
        UserEntity user = UserEntity.builder()
                .username("luca")
                .password(passwordEncoder.encode("password"))
                .build();
        userRepository.save(user);
    }

    @Test
    void login_shouldReturnBadRequest_whenMissingFields() throws Exception {
        LoginRequestDto request = new LoginRequestDto();

        mockMvc.perform(post("/auth/login")
                        .header("X-Forwarded-For", "10.0.0.1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void login_shouldReturnUnauthorized_whenInvalidCredentials() throws Exception {
        LoginRequestDto request = LoginRequestDto.builder()
                .username("luca")
                .password("wrong")
                .build();

        mockMvc.perform(post("/auth/login")
                        .header("X-Forwarded-For", "10.0.0.2")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_shouldReturnOk_whenValidCredentials() throws Exception {
        LoginRequestDto request = LoginRequestDto.builder()
                .username("luca")
                .password("password")
                .build();

        mockMvc.perform(post("/auth/login")
                        .header("X-Forwarded-For", "10.0.0.3")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.user.id").isNumber())
                .andExpect(jsonPath("$.user.username").value("luca"));
    }

    @Test
    void login_shouldReturnTooManyRequests_whenTooManyFailedAttemptsFromSameIp() throws Exception {
        LoginRequestDto request = LoginRequestDto.builder()
                .username("luca")
                .password("wrong")
                .build();

        String blockedIp = "10.0.0.4";

        mockMvc.perform(post("/auth/login")
                        .header("X-Forwarded-For", blockedIp)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/auth/login")
                        .header("X-Forwarded-For", blockedIp)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/auth/login")
                        .header("X-Forwarded-For", blockedIp)
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isTooManyRequests());
    }
}
