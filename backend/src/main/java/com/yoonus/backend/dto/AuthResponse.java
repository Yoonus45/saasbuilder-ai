package com.yoonus.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO returned after a successful registration or login.
 * Contains the JWT token and basic user information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    /** JWT bearer token. */
    private String token;

    /** Database ID of the authenticated user. */
    private Long id;

    /** Display name of the authenticated user. */
    private String name;

    /** Email of the authenticated user. */
    private String email;
}
