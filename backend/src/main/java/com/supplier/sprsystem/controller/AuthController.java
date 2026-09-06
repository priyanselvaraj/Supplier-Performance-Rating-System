package com.supplier.sprsystem.controller;

import com.supplier.sprsystem.dto.request.LoginRequest;
import com.supplier.sprsystem.dto.request.RegisterRequest;
import com.supplier.sprsystem.dto.request.UserUpdateRequest;
import com.supplier.sprsystem.dto.response.ApiResponse;
import com.supplier.sprsystem.dto.response.JwtAuthResponse;
import com.supplier.sprsystem.dto.response.UserResponse;
import com.supplier.sprsystem.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "User registration, login, token verification, and profile APIs")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user credentials and return JWT Bearer token")
    public ResponseEntity<ApiResponse<JwtAuthResponse>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        JwtAuthResponse authResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user account with phone number, email, and password complexity validation")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        UserResponse userResponse = authService.registerUser(registerRequest);
        return new ResponseEntity<>(ApiResponse.success("User registered successfully", userResponse), HttpStatus.CREATED);
    }

    @GetMapping("/me")
    @Operation(summary = "Get currently authenticated user details", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser() {
        UserResponse userResponse = authService.getCurrentUser();
        return ResponseEntity.ok(ApiResponse.success("Current user profile retrieved successfully", userResponse));
    }

    @PutMapping("/profile")
    @Operation(summary = "Update authenticated user's profile, phone number, and email", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<ApiResponse<UserResponse>> updateCurrentProfile(@Valid @RequestBody UserUpdateRequest updateRequest) {
        UserResponse userResponse = authService.updateCurrentProfile(updateRequest);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", userResponse));
    }

    @PostMapping("/logout")
    @Operation(summary = "Stateless logout endpoint (client clears stored JWT token)")
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(ApiResponse.success("User logged out successfully. Please discard the client-side JWT token.", null));
    }
}
