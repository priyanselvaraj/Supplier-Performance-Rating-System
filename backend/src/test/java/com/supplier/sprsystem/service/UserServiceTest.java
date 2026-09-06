package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.response.UserResponse;
import com.supplier.sprsystem.exception.ResourceNotFoundException;
import com.supplier.sprsystem.model.entity.ERole;
import com.supplier.sprsystem.model.entity.Role;
import com.supplier.sprsystem.model.entity.User;
import com.supplier.sprsystem.repository.RoleRepository;
import com.supplier.sprsystem.repository.UserRepository;
import com.supplier.sprsystem.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

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
                .password("encoded_pwd")
                .fullName("John Doe")
                .department("Procurement")
                .active(true)
                .roles(roles)
                .build();
    }

    @Test
    @DisplayName("Test GetAllUsers returns list of users")
    void testGetAllUsers() {
        when(userRepository.findAll()).thenReturn(List.of(sampleUser));

        List<UserResponse> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("john_doe", result.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Test GetUserById with existing ID")
    void testGetUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));

        UserResponse result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals("john_doe", result.getUsername());
        assertEquals("john@example.com", result.getEmail());
    }

    @Test
    @DisplayName("Test GetUserById with non-existing ID throws ResourceNotFoundException")
    void testGetUserById_NotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(99L));
    }

    @Test
    @DisplayName("Test Toggle User Status")
    void testToggleUserStatus() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser));
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        UserResponse response = userService.toggleUserStatus(1L);

        assertNotNull(response);
        assertFalse(response.isActive());
        verify(userRepository, times(1)).save(sampleUser);
    }
}
