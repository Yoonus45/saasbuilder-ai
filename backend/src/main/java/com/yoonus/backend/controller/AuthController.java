package com.yoonus.backend.controller;

import com.yoonus.backend.config.RateLimit;
import com.yoonus.backend.dto.AuthTokenResponse;
import com.yoonus.backend.dto.LoginRequest;
import com.yoonus.backend.dto.RegisterRequest;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.security.JwtUtil;
import com.yoonus.backend.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    @RateLimit(maxRequests = 5, windowSeconds = 60)
    public ResponseEntity<AuthTokenResponse> register(@Valid @RequestBody RegisterRequest request) {
        logger.info("POST /api/auth/register - Incoming request for email: {}", request.getEmail());
        try {
            User user = userService.register(request);
            logger.info("UserService.register() completed successfully, user ID: {}", user.getId());
            
            logger.info("Generating access token...");
            String accessToken = jwtUtil.generateToken(user.getEmail());
            logger.info("Access token generated successfully");
            
            logger.info("Generating refresh token...");
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
            logger.info("Refresh token generated successfully");
            
            long expiresIn = jwtUtil.getExpirationMillis() / 1000;
            AuthTokenResponse response = new AuthTokenResponse(accessToken, refreshToken, expiresIn);
            logger.info("Registration successful, sending response with token expiresIn: {} seconds", expiresIn);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Registration endpoint failed - Exception: {}", e.getMessage(), e);
            throw e;
        }
    }

    @PostMapping("/login")
    @RateLimit(maxRequests = 10, windowSeconds = 60)
    public ResponseEntity<AuthTokenResponse> login(@Valid @RequestBody LoginRequest request) {
        User user = userService.login(request.getEmail(), request.getPassword());
        String accessToken = jwtUtil.generateToken(user.getEmail());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail());
        long expiresIn = jwtUtil.getExpirationMillis() / 1000;
        
        return ResponseEntity.ok(
                new AuthTokenResponse(accessToken, refreshToken, expiresIn)
        );
    }
}