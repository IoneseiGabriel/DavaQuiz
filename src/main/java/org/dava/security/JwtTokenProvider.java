package org.dava.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.extern.slf4j.Slf4j;
import org.dava.exception.InvalidJwtException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

/**
 * Utility component for generating and validating JWT access tokens.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final Algorithm algorithm;
    private final long expirationMillis;

    /**
     * Creates a JWT token provider with a configured secret and expiration time.
     *
     * @param secret           HMAC secret key used to sign and verify tokens
     * @param expirationMillis token validity duration in milliseconds
     */
    public JwtTokenProvider(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expiration-millis:86400000}") long expirationMillis) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expirationMillis = expirationMillis;
    }

    /**
     * Generates a signed JWT token for the given user id.
     */
    public String generateToken(Long userId) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(expirationMillis);

        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(expiresAt))
                .sign(algorithm);
    }

    /**
     * Validates the given JWT token and extracts the user id from its subject.
     * @throws InvalidJwtException if the token cannot be verified or parsed
     */
    public Long validateAndGetUserId(String token) {
        try {
            DecodedJWT jwt = JWT.require(algorithm)
                    .build()
                    .verify(token);

            String subject = jwt.getSubject();
            return Long.parseLong(subject);
        } catch (JWTVerificationException | NumberFormatException e) {
            throw new InvalidJwtException();
        }
    }

}
