package com.backend.demo.service;

import com.backend.demo.dto.auth.LoginResponse;
import com.backend.demo.dto.auth.LoginRequest;

public interface IAuthService {
    LoginResponse login(LoginRequest loginRequest);
}
