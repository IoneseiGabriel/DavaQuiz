package org.dava.security;

import static org.junit.jupiter.api.Assertions.*;

import org.dava.exception.TooManyLoginAttemptsException;
import org.junit.jupiter.api.Test;

class LoginRateLimiterTest {

  @Test
  void checkAllowed_shouldNotThrow_whenNoStateForIp() {
    LoginRateLimiter limiter = new LoginRateLimiter(2, 5, 5);
    assertDoesNotThrow(() -> limiter.checkAllowed("1.2.3.4"));
  }

  @Test
  void registerFailed_shouldRemoveExpiredAttemptsAndBlockWithinWindow()
      throws InterruptedException {
    LoginRateLimiter limiter = new LoginRateLimiter(2, 1, 5);
    String ip = "10.0.0.1";

    limiter.registerFailed(ip);
    assertDoesNotThrow(() -> limiter.checkAllowed(ip));

    Thread.sleep(1100);

    limiter.registerFailed(ip);
    assertDoesNotThrow(() -> limiter.checkAllowed(ip));

    limiter.registerFailed(ip);

    assertThrows(TooManyLoginAttemptsException.class, () -> limiter.checkAllowed(ip));
  }

  @Test
  void checkAllowed_shouldThrowWhileBlockedAndStopThrowingAfterBlockExpires()
      throws InterruptedException {
    LoginRateLimiter limiter = new LoginRateLimiter(1, 60, 1);
    String ip = "192.168.0.10";

    limiter.registerFailed(ip);

    assertThrows(TooManyLoginAttemptsException.class, () -> limiter.checkAllowed(ip));

    Thread.sleep(1100);

    assertDoesNotThrow(() -> limiter.checkAllowed(ip));
    assertDoesNotThrow(() -> limiter.checkAllowed(ip));
  }
}
