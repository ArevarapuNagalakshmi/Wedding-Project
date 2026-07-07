package com.eventplatform.service.auth;

import com.eventplatform.dto.auth.JwtResponse;
import com.eventplatform.dto.auth.LoginRequest;
import com.eventplatform.dto.auth.PasswordResetRequest;
import com.eventplatform.dto.auth.RegisterRequest;

public interface AuthService {
    void register(RegisterRequest request);   // ✅ correct
    JwtResponse login(LoginRequest request);
    void resetPassword(PasswordResetRequest request);
}