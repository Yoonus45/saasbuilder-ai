package com.yoonus.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

/**
 * Utility class responsible for generating, parsing, and validating JWT tokens.
 *
 * <p>Uses JJWT 0.12.x with HS512 signing algorithm. The secret key is read
 * from {@code app.jwt.secret} (Base64-encoded) and the expiry from
 * {@code app.jwt.expiration} (milliseconds).</p>
 */
@Component
public class JwtUtil {

    private final SecretKey signingKey;
    private final long expirationMs;

    /**
     * Constructor injection of JWT configuration values from application properties.
     *
     * @param secret       Base64-encoded HMAC-SHA512 secret key
     * @param expirationMs token validity duration in milliseconds
     */
    public JwtUtil(@Value("${app.jwt.secret}") String secret,
                   @Value("${app.jwt.expiration}") long expirationMs) {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    /**
     * Generate a signed JWT token with the user's email as the subject.
     *
     * @param email the user's email address (used as the JWT subject)
     * @return compact, URL-safe JWT string
     */
    public String generateToken(String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Extract the subject (email) from a JWT token.
     *
     * @param token the compact JWT string
     * @return the email embedded in the token's subject claim
     */
    public String extractEmail(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * Validate a JWT token — checks signature and expiry.
     *
     * @param token the compact JWT string
     * @return {@code true} if valid, {@code false} otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // ── Private helpers ───────────────────────────────────────────────────────

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
