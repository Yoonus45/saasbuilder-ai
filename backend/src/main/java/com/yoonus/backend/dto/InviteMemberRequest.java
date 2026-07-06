package com.yoonus.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class InviteMemberRequest {
    @NotBlank
    @Email
    private String email;

    private String role = "EDITOR";

    public InviteMemberRequest() {
    }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
