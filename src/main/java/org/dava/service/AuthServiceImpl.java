package org.dava.service;

import lombok.RequiredArgsConstructor;
import org.dava.dao.UserRepository;
import org.dava.domain.UserEntity;
import org.dava.dto.LoginRequestDto;
import org.dava.dto.LoginResponseDto;
import org.dava.dto.UserDto;
import org.dava.exception.InvalidCredentialsException;
import org.dava.security.JwtTokenProvider;
import org.dava.security.LoginRateLimiter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for authenticating users and issuing JWT access tokens. The service also
 * integrates with LoginRateLimiter to enforce IP based rate limiting.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final LoginRateLimiter loginRateLimiter;

  /** Authenticates a user for the given client IP and returns a JWT and user data. */
  @Override
  public LoginResponseDto login(LoginRequestDto request, String clientIp) {

    loginRateLimiter.checkAllowed(clientIp); // throws TooManyLoginAttempts exception

    UserEntity user =
        userRepository
            .findByUsername(request.getUsername())
            .orElseThrow(
                () -> {
                  loginRateLimiter.registerFailed(clientIp);
                  return new InvalidCredentialsException();
                });

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      loginRateLimiter.registerFailed(clientIp);
      throw new InvalidCredentialsException();
    }

    String token = jwtTokenProvider.generateToken(user.getId());

    UserDto userDto = UserDto.builder().id(user.getId()).username(user.getUsername()).build();

    return LoginResponseDto.builder().token(token).user(userDto).build();
  }
}
