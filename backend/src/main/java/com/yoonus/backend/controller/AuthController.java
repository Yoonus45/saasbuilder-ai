package com.yoonus.backend.controller;

import com.yoonus.backend.dto.AuthResponse;
import com.yoonus.backend.dto.LoginRequest;
import com.yoonus.backend.dto.RegisterRequest;
import com.yoonus.backend.dto.UserProfileResponse;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for authentication endpoints.
 *
 * <p>All routes under {@code /api/auth} are publicly accessible (configured in
 * {@link com.yoonus.backend.security.SecurityConfig}), except {@code GET /me}
 * which requires a valid JWT token.</p>
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    /**
     * Constructor injection.
     *
     * @param authService    handles registration and login logic
     * @param userRepository used to fetch the full user profile for /me
     */
    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    /**
     * Register a new user account.
     *
     * @param request validated registration payload (name, email, password)
     * @return {@code 201 Created} with an {@link AuthResponse} containing the JWT
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Authenticate an existing user.
     *
     * @param request validated login payload (email, password)
     * @return {@code 200 OK} with an {@link AuthResponse} containing the JWT
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Return the profile of the currently authenticated user.
     *
     * <p>Requires a valid {@code Authorization: Bearer <token>} header.
     * The user's email is extracted from the {@link SecurityContextHolder} via
     * {@link AuthenticationPrincipal}.</p>
     *
     * @param userDetails the authenticated principal injected by Spring Security
     * @return {@code 200 OK} with a {@link UserProfileResponse}
     */
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));

        UserProfileResponse profile = UserProfileResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .createdAt(user.getCreatedAt())
                .build();

        return ResponseEntity.ok(profile);
    }

    /**
     * Global handler for duplicate-email registration attempts.
     *
     * @param ex the exception thrown by {@link AuthService#register}
     * @return {@code 409 Conflict} with an error message
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleConflict(IllegalStateException ex) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }
}
