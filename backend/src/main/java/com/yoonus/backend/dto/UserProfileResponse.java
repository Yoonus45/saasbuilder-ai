package com.yoonus.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO returned by the {@code GET /api/auth/me} protected endpoint.
 * Exposes safe user profile fields — never exposes the hashed password.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    /** Database ID of the user. */
    private Long id;

    /** Full display name. */
    private String name;

    /** Unique email address. */
    private String email;

    /** Role assigned to the user (e.g. "USER", "ADMIN"). */
    private String role;

    /** UTC timestamp of account creation. */
    private LocalDateTime createdAt;
}
