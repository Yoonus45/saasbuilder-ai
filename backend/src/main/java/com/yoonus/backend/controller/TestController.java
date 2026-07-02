package com.yoonus.backend.controller;

import com.yoonus.backend.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {

    private final JwtUtil jwtUtil;

    public TestController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/api/test")
    public String test() {
        return "JWT Authentication Successful!";
    }

    /**
     * Diagnostic endpoint to test token generation
     * Usage: GET /api/test/generate-token?email=test@example.com
     */
    @GetMapping("/api/test/generate-token")
    public ResponseEntity<?> generateTestToken(@RequestParam String email) {
        try {
            String token = jwtUtil.generateToken(email);
            Map<String, String> response = new HashMap<>();
            response.put("token", token);
            response.put("email", email);
            response.put("message", "Use this token in Authorization header: Bearer " + token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }

    /**
     * Diagnostic endpoint to validate token
     * Usage: GET /api/test/validate-token?token=<jwt_token>
     */
    @GetMapping("/api/test/validate-token")
    public ResponseEntity<?> validateToken(@RequestParam String token) {
        Map<String, Object> response = new HashMap<>();
        boolean isValid = jwtUtil.validateToken(token);
        response.put("valid", isValid);
        
        if (isValid) {
            String email = jwtUtil.extractEmail(token);
            response.put("email", email);
            response.put("message", "Token is valid");
        } else {
            response.put("message", "Token is invalid or expired");
        }
        return ResponseEntity.ok(response);
    }

    /**
     * Diagnostic endpoint to check current authentication
     * Usage: GET /api/test/current-auth with Authorization header
     */
    @GetMapping("/api/test/current-auth")
    public ResponseEntity<?> getCurrentAuth(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        if (authentication == null) {
            response.put("authenticated", false);
            response.put("message", "No authentication found");
        } else {
            response.put("authenticated", authentication.isAuthenticated());
            response.put("principal", authentication.getPrincipal());
            response.put("authorities", authentication.getAuthorities());
            response.put("name", authentication.getName());
            response.put("details", authentication.getDetails());
        }
        
        return ResponseEntity.ok(response);
    }
}