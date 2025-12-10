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

    public boolean isAuthorizationTokenValid(String token) {
        token = token.replace("Bearer ", "");
        return jwtTokenProvider.validateAndGetUserId(token) != null;
    }
}
