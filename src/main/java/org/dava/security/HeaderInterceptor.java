package org.dava.security;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * A simple component used to check the Authorization Bearer token.
 * This component will be deleted when the Spring Security will be used.
 */
@Component
@AllArgsConstructor
public class HeaderInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    public void isAuthorizationTokenValid(String token) {
        token = token.replace("Bearer ", "");
        jwtTokenProvider.validateAndGetUserId(token);
    }
}