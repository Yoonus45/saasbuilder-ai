package com.yoonus.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO for the user login request body.
 */
@Data
public class LoginRequest {

    /** The user's email address. */
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    /** The user's plain-text password. */
    @NotBlank(message = "Password is required")
    private String password;
}
