package com.supplier.sprsystem.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Set;

public class UserUpdateRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email
    @Size(max = 100)
    private String email;

    @Size(max = 20)
    private String phone;

    @Size(max = 100)
    private String department;

    private Boolean active;

    private Set<String> roles;

    public UserUpdateRequest() {}

    public UserUpdateRequest(String fullName, String email, String phone, String department, Boolean active, Set<String> roles) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.active = active;
        this.roles = roles;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String fullName;
        private String email;
        private String phone;
        private String department;
        private Boolean active;
        private Set<String> roles;

        public Builder fullName(String fullName) { this.fullName = fullName; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder phone(String phone) { this.phone = phone; return this; }
        public Builder department(String department) { this.department = department; return this; }
        public Builder active(Boolean active) { this.active = active; return this; }
        public Builder roles(Set<String> roles) { this.roles = roles; return this; }

        public UserUpdateRequest build() {
            return new UserUpdateRequest(fullName, email, phone, department, active, roles);
        }
    }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
}
