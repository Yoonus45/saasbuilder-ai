package com.yoonus.backend.controller;

import com.yoonus.backend.dto.LogoutResponse;
import com.yoonus.backend.security.JwtUtil;
import com.yoonus.backend.service.TokenBlacklistService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
public class LogoutController {

    private final JwtUtil jwtUtil;
    private final TokenBlacklistService tokenBlacklistService;

    public LogoutController(JwtUtil jwtUtil, TokenBlacklistService tokenBlacklistService) {
        this.jwtUtil = jwtUtil;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body(new LogoutResponse("Authorization header is missing or malformed"));
        }

        String token = authorizationHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.badRequest().body(new LogoutResponse("Invalid or expired token"));
        }

        Date expiration = jwtUtil.extractExpiration(token);
        tokenBlacklistService.revokeToken(token, expiration != null ? expiration.toInstant() : Instant.now());

        return ResponseEntity.ok(new LogoutResponse("Logged out successfully"));
    }
}
