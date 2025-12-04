package org.dava.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.dava.dto.LoginRequestDto;
import org.dava.dto.LoginResponseDto;
import org.dava.exception.InvalidCredentialsException;
import org.dava.exception.TooManyLoginAttemptsException;
import org.dava.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller that exposes authentication endpoints.
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Authenticates a user based on the provided credentials.
     * @return HTTP 200 with a login response on success, 400, 401 or 429 on error
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto request,
                                   HttpServletRequest httpRequest) {

        String clientIp = extractClientIp(httpRequest);

        try {
            LoginResponseDto response = authService.login(request, clientIp);
            return ResponseEntity.ok(response);
        } catch (TooManyLoginAttemptsException ex) {
            return ResponseEntity
                    .status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(ex.getMessage());
        } catch (InvalidCredentialsException ex) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(ex.getMessage());
        }
    }

    /**
     * Determines the client IP address from the request.
     */
    private String extractClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) {
            return forwarded.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
