package com.smartgarage.api.service;

import com.smartgarage.api.dto.request.LoginRequest;
import com.smartgarage.api.dto.request.RegisterRequest;
import com.smartgarage.api.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}
