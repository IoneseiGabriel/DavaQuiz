package org.dava.security;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.dava.exception.TooManyLoginAttemptsException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * In memory rate limiter that tracks failed login attempts per IP and blocks further attempts for a
 * configured time window.
 */
@Slf4j
@Component
public class LoginRateLimiter {

  private final int maxFailedAttempts;
  private final Duration interval;
  private final Duration blockDuration;

  private final Map<String, IpState> stateByIp = new ConcurrentHashMap<>();

  /**
   * Creates a new rate limiter with configurable thresholds and durations.
   *
   * @param maxFailedAttempts maximum number of failed attempts allowed per interval
   * @param intervalSeconds size of the sliding time window in seconds
   * @param blockSeconds duration in seconds for which an IP is blocked after exceeding the limit
   */
  public LoginRateLimiter(
      @Value("${auth.rate-limit.max-failed-attempts:5}") int maxFailedAttempts,
      @Value("${auth.rate-limit.interval-seconds:900}") long intervalSeconds,
      @Value("${auth.rate-limit.block-seconds:900}") long blockSeconds) {

    this.maxFailedAttempts = maxFailedAttempts;
    this.interval = Duration.ofSeconds(intervalSeconds);
    this.blockDuration = Duration.ofSeconds(blockSeconds);
  }

  /**
   * Verifies if the given IP address is currently allowed to attempt login. If the IP is still
   * blocked, a TooManyLoginAttemptsException is thrown. If the block period has expired, the
   * internal state for that IP is cleared.
   */
  public void checkAllowed(String ip) {
    IpState state = stateByIp.get(ip);
    if (state == null) {
      return;
    }

    Instant now = Instant.now();
    Instant blockedUntil = state.getBlockedUntil();

    if (blockedUntil != null) {
      if (now.isBefore(blockedUntil)) {
        long remainingSeconds = Duration.between(now, blockedUntil).getSeconds();
        throw new TooManyLoginAttemptsException(remainingSeconds);
      } else {
        stateByIp.remove(ip);
      }
    }
  }

  /**
   * Registers a failed login attempt for the given IP address. Old attempts outside the configured
   * interval are discarded. When the number of recent failed attempts reaches the configured
   * threshold, the IP is blocked for the configured block duration.
   */
  public void registerFailed(String ip) {
    Instant now = Instant.now();

    IpState state = stateByIp.computeIfAbsent(ip, k -> new IpState(new ArrayDeque<>(), null));

    Deque<Instant> attempts = state.getFailedAttempts();

    Instant windowStart = now.minus(interval);
    while (!attempts.isEmpty() && attempts.peekFirst().isBefore(windowStart)) {
      attempts.removeFirst();
    }

    attempts.addLast(now);

    if (attempts.size() >= maxFailedAttempts) {
      Instant blockedUntil = now.plus(blockDuration);
      state.setBlockedUntil(blockedUntil);
      log.warn(
          "IP {} blocked until {} after {} failed attempts in the last {} seconds",
          ip,
          blockedUntil,
          attempts.size(),
          interval.getSeconds());
    }
  }

  /**
   * Internal state for a single IP address, holding timestamps of failed attempts and the optional
   * block expiration time.
   */
  @Data
  @AllArgsConstructor
  private static class IpState {
    private Deque<Instant> failedAttempts;
    private Instant blockedUntil;
  }
}
