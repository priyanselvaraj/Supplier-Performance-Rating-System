package com.supplier.sprsystem.dto.request;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    private String username;
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    public LoginRequest() {}

    public LoginRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String username;
        private String email;
        private String password;

        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }

        public LoginRequest build() {
            return new LoginRequest(username, email, password);
        }
    }

    public String getUsername() {
        if (username != null && !username.trim().isEmpty()) {
            return username.trim();
        }
        return email != null ? email.trim() : null;
    }

    public void setUsername(String username) { this.username = username; }

    public String getEmail() {
        if (email != null && !email.trim().isEmpty()) {
            return email.trim();
        }
        return username != null ? username.trim() : null;
    }

    public void setEmail(String email) { this.email = email; }

    public String getUsernameOrEmail() {
        if (username != null && !username.trim().isEmpty()) {
            return username.trim();
        }
        return email != null ? email.trim() : "";
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
