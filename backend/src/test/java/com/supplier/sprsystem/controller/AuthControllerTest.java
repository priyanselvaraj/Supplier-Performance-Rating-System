package com.supplier.sprsystem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.supplier.sprsystem.dto.request.LoginRequest;
import com.supplier.sprsystem.dto.request.RegisterRequest;
import com.supplier.sprsystem.dto.response.JwtAuthResponse;
import com.supplier.sprsystem.dto.response.UserResponse;
import com.supplier.sprsystem.exception.GlobalExceptionHandler;
import com.supplier.sprsystem.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthControllerTest {

    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/auth/register - Success")
    void testRegisterUser_Success() throws Exception {
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("newmanager")
                .email("manager@example.com")
                .password("Password@123")
                .confirmPassword("Password@123")
                .fullName("New Manager")
                .phone("+1 555-0100")
                .department("Procurement")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .username("newmanager")
                .email("manager@example.com")
                .fullName("New Manager")
                .phone("+1 555-0100")
                .roles(Set.of("ROLE_MANAGER"))
                .active(true)
                .build();

        when(authService.registerUser(any(RegisterRequest.class))).thenReturn(userResponse);

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("User registered successfully"))
                .andExpect(jsonPath("$.data.username").value("newmanager"))
                .andExpect(jsonPath("$.data.email").value("manager@example.com"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login - Success")
    void testLoginUser_Success() throws Exception {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("admin")
                .password("Admin@12345")
                .build();

        JwtAuthResponse authResponse = JwtAuthResponse.builder()
                .token("mock-jwt-token")
                .type("Bearer")
                .tokenType("Bearer")
                .id(1L)
                .userId(1L)
                .username("admin")
                .email("admin@sprsystem.com")
                .fullName("System Admin")
                .roles(List.of("ROLE_ADMIN"))
                .build();

        when(authService.authenticateUser(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Login successful"))
                .andExpect(jsonPath("$.data.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    @DisplayName("GET /api/v1/auth/me - Success")
    void testGetCurrentUser_Success() throws Exception {
        UserResponse userResponse = UserResponse.builder()
                .id(1L)
                .username("admin")
                .email("admin@sprsystem.com")
                .fullName("System Admin")
                .phone("+1 555-0100")
                .roles(Set.of("ROLE_ADMIN"))
                .active(true)
                .build();

        when(authService.getCurrentUser()).thenReturn(userResponse);

        mockMvc.perform(get("/api/v1/auth/me")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.email").value("admin@sprsystem.com"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/logout - Success")
    void testLogout_Success() throws Exception {
        mockMvc.perform(post("/api/v1/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
