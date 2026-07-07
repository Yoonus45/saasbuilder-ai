package com.yoonus.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for the user registration request body.
 */
@Data
public class RegisterRequest {

    /** Full display name. */
    @NotBlank(message = "Name is required")
    private String name;

    /** Email used as login identifier. */
    @NotBlank(message = "Email is required")
    @Email(message = "Must be a valid email address")
    private String email;

    /** Plain-text password — will be hashed before storage. */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
