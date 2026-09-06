package com.supplier.sprsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.HashSet;
import java.util.Set;

public class RegisterRequest {

    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    @Size(max = 100, message = "Email cannot exceed 100 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Pattern(
            regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!._\\-]).{8,}$",
            message = "Password must be at least 8 characters long, contain at least 1 uppercase letter, 1 lowercase letter, 1 number, and 1 special symbol (@$!%*?&)"
    )
    private String password;

    private String confirmPassword;

    @NotBlank(message = "Full name is required")
    @Size(max = 100, message = "Full name cannot exceed 100 characters")
    private String fullName;

    @NotBlank(message = "Phone number is required")
    @Size(max = 20, message = "Phone number cannot exceed 20 characters")
    private String phone;

    @Size(max = 100, message = "Department cannot exceed 100 characters")
    private String department;

    private String role;
    private Set<String> roles;
    private Long supplierId;

    public RegisterRequest() {}

    public RegisterRequest(String username, String email, String password, String confirmPassword, String fullName, String phone, String department, String role, Set<String> roles) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.fullName = fullName;
        this.phone = phone;
        this.department = department;
        this.role = role;
        this.roles = roles;
    }

    public RegisterRequest(String username, String email, String password, String confirmPassword, String fullName, String phone, String department, String role, Set<String> roles, Long supplierId) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.fullName = fullName;
        this.phone = phone;
        this.department = department;
        this.role = role;
        this.roles = roles;
        this.supplierId = supplierId;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String email;
        private String password;
        private String confirmPassword;
        private String fullName;
        private String phone;
        private String department;
        private String role;
        private Set<String> roles;
        private Long supplierId;

        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder confirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phone = phoneNumber; return this; }
        public Builder department(String department) { this.department = department; return this; }
        public Builder role(String role) { this.role = role; return this; }
        public Builder roles(Set<String> roles) { this.roles = roles; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }

        public RegisterRequest build() {
            return new RegisterRequest(username, email, password, confirmPassword, fullName, phone, department, role, roles, supplierId);
        }
    }

    public String getUsername() {
        if (username != null && !username.trim().isEmpty()) {
            return username.trim();
        }
        if (email != null && email.contains("@")) {
            return email.substring(0, email.indexOf('@')).trim();
        }
        return email;
    }

    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getPhoneNumber() { return phone; }
    public void setPhoneNumber(String phoneNumber) { this.phone = phoneNumber; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }

    public Set<String> getRoles() {
        if (roles != null && !roles.isEmpty()) {
            return roles;
        }
        if (role != null && !role.trim().isEmpty()) {
            Set<String> rSet = new HashSet<>();
            rSet.add(role.trim());
            return rSet;
        }
        return roles;
    }

    public void setRoles(Set<String> roles) { this.roles = roles; }
}
