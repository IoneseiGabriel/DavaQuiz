package org.dava.service;

import org.dava.dao.UserRepository;
import org.dava.domain.UserEntity;
import org.dava.dto.LoginRequestDto;
import org.dava.dto.LoginResponseDto;
import org.dava.exception.InvalidCredentialsException;
import org.dava.exception.TooManyLoginAttemptsException;
import org.dava.security.JwtTokenProvider;
import org.dava.security.LoginRateLimiter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private LoginRateLimiter loginRateLimiter;

    @InjectMocks
    private AuthServiceImpl authService;

    @Test
    void login_shouldReturnResponse_whenCredentialsAreValid() {
        String ip = "1.2.3.4";
        LoginRequestDto request = LoginRequestDto.builder()
                .username("luca")
                .password("pass")
                .build();

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("luca")
                .password("hashed")
                .build();

        when(userRepository.findByUsername("luca")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("pass", "hashed")).thenReturn(true);
        when(jwtTokenProvider.generateToken(1L)).thenReturn("jwt-token");

        LoginResponseDto response = authService.login(request, ip);

        verify(loginRateLimiter).checkAllowed(ip);
        verify(loginRateLimiter, never()).registerFailed(ip);
        verify(jwtTokenProvider).generateToken(1L);

        assertEquals("jwt-token", response.getToken());
        assertEquals(1L, response.getUser().getId());
        assertEquals("luca", response.getUser().getUsername());
    }

    @Test
    void login_shouldRegisterFailedAndThrow_whenUserNotFound() {
        String ip = "1.2.3.4";
        LoginRequestDto request = LoginRequestDto.builder()
                .username("unknown")
                .password("pass")
                .build();

        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(request, ip));

        verify(loginRateLimiter).checkAllowed(ip);
        verify(loginRateLimiter).registerFailed(ip);
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void login_shouldRegisterFailedAndThrow_whenPasswordInvalid() {
        String ip = "1.2.3.4";
        LoginRequestDto request = LoginRequestDto.builder()
                .username("luca")
                .password("wrong")
                .build();

        UserEntity user = UserEntity.builder()
                .id(1L)
                .username("luca")
                .password("hashed")
                .build();

        when(userRepository.findByUsername("luca")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong", "hashed")).thenReturn(false);

        assertThrows(InvalidCredentialsException.class,
                () -> authService.login(request, ip));

        verify(loginRateLimiter).checkAllowed(ip);
        verify(loginRateLimiter).registerFailed(ip);
        verifyNoInteractions(jwtTokenProvider);
    }

    @Test
    void login_shouldPropagateTooManyLoginAttemptsException_whenIpBlocked() {
        String ip = "1.2.3.4";
        LoginRequestDto request = LoginRequestDto.builder()
                .username("luca")
                .password("pass")
                .build();

        doThrow(new TooManyLoginAttemptsException(10L))
                .when(loginRateLimiter).checkAllowed(ip);

        assertThrows(TooManyLoginAttemptsException.class,
                () -> authService.login(request, ip));

        verify(loginRateLimiter).checkAllowed(ip);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(passwordEncoder);
        verifyNoInteractions(jwtTokenProvider);
    }
}
