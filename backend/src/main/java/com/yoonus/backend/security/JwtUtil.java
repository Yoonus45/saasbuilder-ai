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

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration.hours:24}")
    private long expirationHours;

    @Value("${jwt.refresh.expiration.days:30}")
    private long refreshExpirationDays;

    private Key key;

    private Key getKey() {
        if (key == null) {
            if (secret == null || secret.isBlank()) {
                logger.error("JWT secret is not configured (property 'jwt.secret' is empty)");
                throw new IllegalStateException("JWT secret is not configured. Set 'jwt.secret' environment variable or property.");
            }
            try {
                key = Keys.hmacShaKeyFor(secret.getBytes());
            } catch (IllegalArgumentException e) {
                logger.error("Invalid JWT secret provided: {}", e.getMessage());
                throw new IllegalStateException("JWT secret is invalid or too short. Provide a secure 32+ byte secret.", e);
            }
        }
        return key;
    }

    public String generateToken(String email) {
        try {
            long expirationMillis = 1000 * 60 * 60 * expirationHours;
            String token = Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                    .claim("type", "access")
                    .signWith(getKey(), SignatureAlgorithm.HS256)
                    .compact();
            logger.debug("Access token generated for email: {}", email);
            return token;
        } catch (Exception e) {
            logger.error("Error generating token for email: {}", email, e);
            throw e;
        }
    }

    public String generateRefreshToken(String email) {
        try {
            long expirationMillis = 1000 * 60 * 60 * 24 * refreshExpirationDays;
            String token = Jwts.builder()
                    .setSubject(email)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
                    .claim("type", "refresh")
                    .signWith(getKey(), SignatureAlgorithm.HS256)
                    .compact();
            logger.debug("Refresh token generated for email: {}", email);
            return token;
        } catch (Exception e) {
            logger.error("Error generating refresh token for email: {}", email, e);
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

    public Date extractExpiration(String token) {
        try {
            if (token == null || token.isEmpty()) {
                return null;
            }
            return Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getExpiration();
        } catch (Exception e) {
            logger.error("Error extracting expiration from token", e);
            return null;
        }
    }

    public long getExpirationMillis() {
        return 1000 * 60 * 60 * expirationHours;
    }

    public long getRefreshExpirationMillis() {
        return 1000 * 60 * 60 * 24 * refreshExpirationDays;
    }
}