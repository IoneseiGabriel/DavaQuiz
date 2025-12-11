package org.dava.service;

import org.dava.dto.LoginRequestDto;
import org.dava.dto.LoginResponseDto;

public interface AuthService {

  LoginResponseDto login(LoginRequestDto request, String clientIp);
}
