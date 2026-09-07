package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.LoginRequest;
import com.supplier.sprsystem.dto.request.RegisterRequest;
import com.supplier.sprsystem.dto.response.JwtAuthResponse;
import com.supplier.sprsystem.dto.response.UserResponse;
import com.supplier.sprsystem.exception.BadRequestException;
import com.supplier.sprsystem.exception.DuplicateResourceException;
import com.supplier.sprsystem.exception.UnauthorizedException;
import com.supplier.sprsystem.model.entity.ERole;
import com.supplier.sprsystem.model.entity.Role;
import com.supplier.sprsystem.model.entity.User;
import com.supplier.sprsystem.repository.RoleRepository;
import com.supplier.sprsystem.repository.UserRepository;
import com.supplier.sprsystem.security.jwt.JwtUtils;
import com.supplier.sprsystem.security.services.UserDetailsImpl;
import com.supplier.sprsystem.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private com.supplier.sprsystem.security.jwt.JwtService jwtUtils;

    @Mock
    private NotificationService notificationService;

    @Mock
    private EmailService emailService;

    @Mock
    private SmsService smsService;

    @InjectMocks
    private AuthServiceImpl authService;

    private User sampleUser;
    private Role managerRole;

    @BeforeEach
    void setUp() {
        managerRole = Role.builder().id(1L).name(ERole.ROLE_MANAGER).build();
        Set<Role> roles = new HashSet<>();
        roles.add(managerRole);

        sampleUser = User.builder()
                .id(1L)
                .username("john_doe")
                .email("john@example.com")
                .password("encodedPassword123")
                .fullName("John Doe")
                .phone("+1 555-0199")
                .department("Procurement")
                .active(true)
                .roles(roles)
                .build();
    }

    @Test
    @DisplayName("Test successful user registration")
    void testRegisterUser_Success() {
        RegisterRequest request = RegisterRequest.builder()
                .username("new_user")
                .email("new_user@example.com")
                .password("SecurePass@123")
                .confirmPassword("SecurePass@123")
                .fullName("New User")
                .phone("+1 555-0123")
                .department("Quality")
                .role("MANAGER")
                .build();

        when(userRepository.existsByUsername("new_user")).thenReturn(false);
        when(userRepository.existsByEmail("new_user@example.com")).thenReturn(false);
        when(roleRepository.findByName(ERole.ROLE_MANAGER)).thenReturn(Optional.of(managerRole));
        when(passwordEncoder.encode("SecurePass@123")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponse response = authService.registerUser(request);

        assertNotNull(response);
        assertEquals("john_doe", response.getUsername());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendWelcomeEmail(any(User.class));
        verify(smsService, times(1)).sendWelcomeSms(any(User.class));
        verify(notificationService, times(1)).createNotification(any(), anyString(), anyString(), any(), any(), anyString(), any());
    }

    @Test
    @DisplayName("Test registration with duplicate email throws DuplicateResourceException")
    void testRegisterUser_DuplicateEmail_ThrowsException() {
        RegisterRequest request = RegisterRequest.builder()
                .username("unique_user")
                .email("john@example.com")
                .password("SecurePass@123")
                .confirmPassword("SecurePass@123")
                .fullName("Duplicate Email User")
                .phone("+1 555-0123")
                .build();

        when(userRepository.existsByUsername("unique_user")).thenReturn(false);
        when(userRepository.existsByEmail("john@example.com")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.registerUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Test registration with duplicate username throws DuplicateResourceException")
    void testRegisterUser_DuplicateUsername_ThrowsException() {
        RegisterRequest request = RegisterRequest.builder()
                .username("john_doe")
                .email("unique@example.com")
                .password("SecurePass@123")
                .confirmPassword("SecurePass@123")
                .fullName("Duplicate Username User")
                .phone("+1 555-0123")
                .build();

        when(userRepository.existsByUsername("john_doe")).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> authService.registerUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Test registration with password mismatch throws BadRequestException")
    void testRegisterUser_PasswordMismatch_ThrowsException() {
        RegisterRequest request = RegisterRequest.builder()
                .username("mismatch_user")
                .email("mismatch@example.com")
                .password("SecurePass@123")
                .confirmPassword("DifferentPass@456")
                .fullName("Mismatch User")
                .phone("+1 555-0123")
                .build();

        assertThrows(BadRequestException.class, () -> authService.registerUser(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Test successful login with username and password")
    void testAuthenticateUser_Success() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("john_doe")
                .password("SecurePass@123")
                .build();

        UserDetailsImpl userDetails = new UserDetailsImpl(
                1L,
                "john_doe",
                "john@example.com",
                "John Doe",
                "encodedPassword123",
                true,
                List.of(new SimpleGrantedAuthority("ROLE_MANAGER"))
        );

        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(sampleUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);
        when(jwtUtils.generateJwtToken(auth)).thenReturn("mockJwtToken123");

        JwtAuthResponse response = authService.authenticateUser(loginRequest);

        assertNotNull(response);
        assertEquals("mockJwtToken123", response.getToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("john_doe", response.getUsername());
        assertEquals("john@example.com", response.getEmail());
        assertTrue(response.getRoles().contains("ROLE_MANAGER"));

        verify(emailService, times(1)).sendLoginAlertEmail(any(User.class), anyString(), anyString(), any());
        verify(smsService, times(1)).sendLoginAlertSms(any(User.class), anyString(), any());
        verify(notificationService, times(1)).createNotification(any(), anyString(), anyString(), any(), any(), anyString(), any());
    }

    @Test
    @DisplayName("Test login with invalid password throws UnauthorizedException")
    void testAuthenticateUser_InvalidCredentials_ThrowsException() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("john_doe")
                .password("WrongPassword@123")
                .build();

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(sampleUser));
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(UnauthorizedException.class, () -> authService.authenticateUser(loginRequest));
    }

    @Test
    @DisplayName("Test login with disabled account throws UnauthorizedException")
    void testAuthenticateUser_DisabledAccount_ThrowsException() {
        sampleUser.setActive(false);

        LoginRequest loginRequest = LoginRequest.builder()
                .username("john_doe")
                .password("SecurePass@123")
                .build();

        when(userRepository.findByUsername("john_doe")).thenReturn(Optional.of(sampleUser));

        assertThrows(UnauthorizedException.class, () -> authService.authenticateUser(loginRequest));
        verify(authenticationManager, never()).authenticate(any());
    }

    @Test
    @DisplayName("Test login with non-existent user throws UnauthorizedException")
    void testAuthenticateUser_UserNotFound_ThrowsException() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("unknown_user")
                .password("SecurePass@123")
                .build();

        when(userRepository.findByUsername("unknown_user")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("unknown_user")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.authenticateUser(loginRequest));
    }
}
