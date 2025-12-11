package org.dava.security;

import static org.junit.jupiter.api.Assertions.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.dava.exception.InvalidJwtException;
import org.junit.jupiter.api.Test;

class JwtTokenProviderTest {

  @Test
  void generateToken_andValidate_shouldReturnUserId() {
    JwtTokenProvider provider = new JwtTokenProvider("test-secret", 1000);
    Long userId = 42L;

    String token = provider.generateToken(userId);
    assertNotNull(token);

    Long extractedUserId = provider.validateAndGetUserId(token);
    assertEquals(userId, extractedUserId);
  }

  @Test
  void validateAndGetUserId_shouldThrowInvalidJwtException_whenTokenIsTampered() {
    JwtTokenProvider provider = new JwtTokenProvider("test-secret", 1000);
    Long userId = 99L;

    String validToken = provider.generateToken(userId);
    String tamperedToken = validToken + "x";

    assertThrows(InvalidJwtException.class, () -> provider.validateAndGetUserId(tamperedToken));
  }

  @Test
  void validateAndGetUserId_shouldThrowInvalidJwtException_whenSubjectIsNotNumeric() {
    String secret = "test-secret";
    Algorithm algorithm = Algorithm.HMAC256(secret);
    JwtTokenProvider provider = new JwtTokenProvider(secret, 1000);

    String tokenWithNonNumericSubject = JWT.create().withSubject("not-a-number").sign(algorithm);

    assertThrows(
        InvalidJwtException.class, () -> provider.validateAndGetUserId(tokenWithNonNumericSubject));
  }

  @Test
  void validateAndGetUserId_shouldThrowInvalidJwtException_whenTokenIsExpired()
      throws InterruptedException {
    JwtTokenProvider provider = new JwtTokenProvider("test-secret", 1000);

    String token = provider.generateToken(1L);

    Thread.sleep(1100);

    assertThrows(InvalidJwtException.class, () -> provider.validateAndGetUserId(token));
  }
}
