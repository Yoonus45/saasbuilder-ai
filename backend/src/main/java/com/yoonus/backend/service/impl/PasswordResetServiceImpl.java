package com.yoonus.backend.service.impl;

import com.yoonus.backend.dto.PasswordResetConfirmRequest;
import com.yoonus.backend.entity.PasswordResetToken;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.exception.ResourceNotFoundException;
import com.yoonus.backend.repository.PasswordResetTokenRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.security.JwtUtil;
import com.yoonus.backend.service.PasswordResetService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository resetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public PasswordResetServiceImpl(
            UserRepository userRepository,
            PasswordResetTokenRepository resetTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.resetTokenRepository = resetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    @Transactional
    public void requestPasswordReset(String email) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Invalidate previous tokens
        resetTokenRepository.findByUserAndUsedFalse(user)
                .ifPresent(token -> token.setUsed(true));

        // Generate new reset token (24 hour validity)
        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(24);

        PasswordResetToken token = new PasswordResetToken(user, resetToken, expiresAt);
        resetTokenRepository.save(token);

        // In production, send email with reset link containing the token.
    }

    @Override
    @Transactional
    public void confirmPasswordReset(PasswordResetConfirmRequest request) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(request.getResetToken())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid reset token"));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Reset token has already been used");
        }

        if (LocalDateTime.now().isAfter(resetToken.getExpiresAt())) {
            throw new IllegalArgumentException("Reset token has expired");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);
    }

    @Override
    public boolean isResetTokenValid(String token) {
        return resetTokenRepository.findByToken(token)
                .map(resetToken -> !resetToken.isUsed() && 
                     LocalDateTime.now().isBefore(resetToken.getExpiresAt()))
                .orElse(false);
    }
}
