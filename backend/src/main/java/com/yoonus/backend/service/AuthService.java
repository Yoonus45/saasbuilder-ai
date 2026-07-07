package com.yoonus.backend.service;

import com.yoonus.backend.dto.AuthResponse;
import com.yoonus.backend.dto.LoginRequest;
import com.yoonus.backend.dto.RegisterRequest;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.security.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service handling user registration and login business logic.
 *
 * <p>Follows SOLID principles — single responsibility of authentication operations,
 * with dependencies injected via constructor.</p>
 */
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    /**
     * Constructor injection of all required dependencies.
     *
     * @param userRepository        for persisting and querying users
     * @param passwordEncoder       BCrypt encoder for hashing passwords
     * @param jwtUtil               JWT token generator
     * @param authenticationManager Spring Security authentication manager
     */
    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
    }

    /**
     * Register a new user.
     *
     * <p>Validates that the email is not already taken, hashes the password with
     * BCrypt, persists the user, and returns a JWT token.</p>
     *
     * @param request the registration details (name, email, plain-text password)
     * @return an {@link AuthResponse} containing the JWT and user info
     * @throws IllegalStateException if the email is already registered
     */
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email is already registered: " + request.getEmail());
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(user);
        String token = jwtUtil.generateToken(savedUser.getEmail());

        return AuthResponse.builder()
                .token(token)
                .id(savedUser.getId())
                .name(savedUser.getName())
                .email(savedUser.getEmail())
                .build();
    }

    /**
     * Authenticate an existing user and return a new JWT token.
     *
     * <p>Delegates credential verification to Spring Security's
     * {@link AuthenticationManager} which uses BCrypt comparison internally.</p>
     *
     * @param request the login credentials (email, plain-text password)
     * @return an {@link AuthResponse} containing the JWT and user info
     * @throws org.springframework.security.core.AuthenticationException if credentials are invalid
     */
    public AuthResponse login(LoginRequest request) {
        // Throws AuthenticationException on bad credentials — Security handles the response
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        String token = jwtUtil.generateToken(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }
}
