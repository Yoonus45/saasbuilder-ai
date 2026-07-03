package com.yoonus.backend.controller;

import com.yoonus.backend.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @deprecated Test endpoints - only for development
 * Remove in production
 */
@RestController
@Deprecated
public class TestController {

    private final JwtUtil jwtUtil;

    public TestController(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/api/health")
    public String health() {
        return "OK";
    }

    @GetMapping("/api/test")
    public String test() {
        return "JWT Authentication Successful!";
    }

    /**
     * DEPRECATED: Only for local testing
     */
    @GetMapping("/api/test/generate-token")
    public ResponseEntity<?> generateTestToken(@RequestParam String email) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "This endpoint is deprecated");
        return ResponseEntity.status(403).body(response);
    }

    /**
     * DEPRECATED: Only for local testing
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

    @GetMapping("/api/test/current-auth")
    public ResponseEntity<?> getCurrentAuth(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        
        if (authentication == null) {
            response.put("authenticated", false);
            response.put("message", "No authentication found");
        } else {
            response.put("authenticated", authentication.isAuthenticated());
            response.put("name", authentication.getName());
        }
        
        return ResponseEntity.ok(response);
    }
}