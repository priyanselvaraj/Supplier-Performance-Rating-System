package com.supplier.sprsystem.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;

public class UserRoleUpdateRequest {

    @NotEmpty(message = "At least one role must be provided")
    private Set<String> roles;

    public UserRoleUpdateRequest() {}

    public UserRoleUpdateRequest(Set<String> roles) {
        this.roles = roles;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Set<String> roles;

        public Builder roles(Set<String> roles) { this.roles = roles; return this; }

        public UserRoleUpdateRequest build() {
            return new UserRoleUpdateRequest(roles);
        }
    }

    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
