package com.yoonus.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret:mysecretkeymysecretkeymysecretkey123456}")
    private String secret;

    private Key key;

    // Initialize key after secret is injected
    private Key getKey() {
        if (key == null && secret != null) {
            key = Keys.hmacShaKeyFor(secret.getBytes());
        }
        return key;
    }

    public String generateToken(String email) {
        try {
            String token = Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hour
                    .signWith(getKey(), SignatureAlgorithm.HS256)
                    .compact();
            logger.debug("Token generated for email: {}", email);
            return token;
        } catch (Exception e) {
            logger.error("Error generating token for email: {}", email, e);
            throw e;
        }
    }

    public boolean validateToken(String token) {
        try {
            if (token == null || token.isEmpty()) {
                logger.warn("Token is null or empty");
                return false;
            }
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            logger.debug("Token validation successful");
            return true;
        } catch (ExpiredJwtException e) {
            logger.warn("Token has expired");
            return false;
        } catch (JwtException e) {
            logger.warn("JWT validation failed: {}", e.getMessage());
            return false;
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid JWT: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            logger.error("Unexpected error validating token", e);
            return false;
        }
    }

    public String extractEmail(String token) {
        try {
            if (token == null || token.isEmpty()) {
                logger.warn("Cannot extract email from null or empty token");
                return null;
            }
            String email = Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
            logger.debug("Email extracted from token: {}", email);
            return email;
        } catch (Exception e) {
            logger.error("Error extracting email from token", e);
            return null;
        }
    }
}