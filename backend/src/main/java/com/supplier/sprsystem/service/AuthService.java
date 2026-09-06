package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.LoginRequest;
import com.supplier.sprsystem.dto.request.RegisterRequest;
import com.supplier.sprsystem.dto.request.UserUpdateRequest;
import com.supplier.sprsystem.dto.response.JwtAuthResponse;
import com.supplier.sprsystem.dto.response.UserResponse;

public interface AuthService {
    JwtAuthResponse authenticateUser(LoginRequest loginRequest);
    UserResponse registerUser(RegisterRequest registerRequest);
    UserResponse getCurrentUser();
    UserResponse updateCurrentProfile(UserUpdateRequest updateRequest);
}
