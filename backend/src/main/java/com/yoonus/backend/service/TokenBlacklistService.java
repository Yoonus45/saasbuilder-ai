package com.yoonus.backend.service;

import com.yoonus.backend.entity.RevokedToken;
import com.yoonus.backend.repository.RevokedTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class TokenBlacklistService {

    private final RevokedTokenRepository revokedTokenRepository;

    public TokenBlacklistService(RevokedTokenRepository revokedTokenRepository) {
        this.revokedTokenRepository = revokedTokenRepository;
    }

    public boolean isRevoked(String token) {
        return revokedTokenRepository.existsByToken(token);
    }

    @Transactional
    public void revokeToken(String token, Instant expiresAt) {
        if (token == null || token.isBlank()) {
            return;
        }
        Optional<RevokedToken> existingToken = revokedTokenRepository.findByToken(token);
        if (existingToken.isPresent()) {
            return;
        }

        RevokedToken revokedToken = new RevokedToken(token, expiresAt != null ? expiresAt : Instant.now());
        revokedTokenRepository.save(revokedToken);
    }
}
