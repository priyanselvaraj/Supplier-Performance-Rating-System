package com.supplier.sprsystem.dto.response;

import java.util.List;

public class JwtAuthResponse {

    private String token;
    private String type = "Bearer";
    private String tokenType = "Bearer";
    private Long id;
    private Long userId;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private List<String> roles;
    private Long supplierId;
    private String supplierName;

    public JwtAuthResponse() {}

    public JwtAuthResponse(String token, String type, Long id, String username, String email, String fullName, List<String> roles, Long supplierId, String supplierName) {
        this.token = token;
        this.type = type != null ? type : "Bearer";
        this.tokenType = this.type;
        this.id = id;
        this.userId = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.roles = roles;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        if (roles != null && !roles.isEmpty()) {
            this.role = roles.get(0).replace("ROLE_", "");
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String token;
        private String type = "Bearer";
        private Long id;
        private String username;
        private String email;
        private String fullName;
        private String role;
        private List<String> roles;
        private Long supplierId;
        private String supplierName;

        public Builder token(String token) { this.token = token; return this; }
        public Builder type(String type) { this.type = type; return this; }
        public Builder tokenType(String tokenType) { this.type = tokenType; return this; }
        public Builder id(Long id) { this.id = id; return this; }
        public Builder userId(Long userId) { this.id = userId; return this; }
        public Builder username(String username) { this.username = username; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder role(String role) { this.role = role; return this; }
        public Builder roles(List<String> roles) { this.roles = roles; return this; }
        public Builder supplierId(Long supplierId) { this.supplierId = supplierId; return this; }
        public Builder supplierName(String supplierName) { this.supplierName = supplierName; return this; }

        public JwtAuthResponse build() {
            JwtAuthResponse resp = new JwtAuthResponse(token, type, id, username, email, fullName, roles, supplierId, supplierName);
            if (this.role != null) {
                resp.setRole(this.role);
            }
            return resp;
        }
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getType() { return type; }
    public void setType(String type) {
        this.type = type;
        this.tokenType = type;
    }
    public String getTokenType() { return tokenType != null ? tokenType : type; }
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
        this.type = tokenType;
    }
    public Long getId() { return id; }
    public void setId(Long id) {
        this.id = id;
        this.userId = id;
    }
    public Long getUserId() { return userId != null ? userId : id; }
    public void setUserId(Long userId) {
        this.userId = userId;
        this.id = userId;
    }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public List<String> getRoles() { return roles; }
    public void setRoles(List<String> roles) {
        this.roles = roles;
        if (roles != null && !roles.isEmpty() && this.role == null) {
            this.role = roles.get(0).replace("ROLE_", "");
        }
    }
    public Long getSupplierId() { return supplierId; }
    public void setSupplierId(Long supplierId) { this.supplierId = supplierId; }
    public String getSupplierName() { return supplierName; }
    public void setSupplierName(String supplierName) { this.supplierName = supplierName; }
}
