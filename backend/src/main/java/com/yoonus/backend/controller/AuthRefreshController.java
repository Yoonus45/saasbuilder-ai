package com.yoonus.backend.controller;

import com.yoonus.backend.dto.AuthTokenResponse;
import com.yoonus.backend.dto.RefreshTokenRequest;
import com.yoonus.backend.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthRefreshController {

    private final JwtUtil jwtUtil;

    public AuthRefreshController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthTokenResponse> refresh(@Valid @RequestBody RefreshTokenRequest request) {
        if (!jwtUtil.validateToken(request.getRefreshToken())) {
            return ResponseEntity.badRequest().build();
        }

        String email = jwtUtil.extractEmail(request.getRefreshToken());
        if (email == null) {
            return ResponseEntity.badRequest().build();
        }

        String newAccessToken = jwtUtil.generateToken(email);
        long expiresIn = jwtUtil.getExpirationMillis() / 1000; // Convert to seconds

        return ResponseEntity.ok(
                new AuthTokenResponse(newAccessToken, request.getRefreshToken(), expiresIn)
        );
    }
}
