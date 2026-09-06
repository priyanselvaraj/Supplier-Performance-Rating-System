package com.supplier.sprsystem.service;

import com.supplier.sprsystem.dto.request.PasswordChangeRequest;
import com.supplier.sprsystem.dto.request.UserRoleUpdateRequest;
import com.supplier.sprsystem.dto.request.UserUpdateRequest;
import com.supplier.sprsystem.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse getUserByUsername(String username);
    List<UserResponse> searchUsers(String keyword);
    UserResponse updateUser(Long id, UserUpdateRequest updateRequest);
    UserResponse updateUserRoles(Long id, UserRoleUpdateRequest roleUpdateRequest);
    UserResponse toggleUserStatus(Long id);
    void changePassword(Long userId, PasswordChangeRequest passwordChangeRequest);
    void deleteUser(Long id);
}
