package com.yoonus.backend.service;

import com.yoonus.backend.dto.PasswordResetConfirmRequest;

public interface PasswordResetService {

    /**
     * Request password reset for user
     */
    void requestPasswordReset(String email);

    /**
     * Confirm password reset with token
     */
    void confirmPasswordReset(PasswordResetConfirmRequest request);

    /**
     * Verify if reset token is valid
     */
    boolean isResetTokenValid(String token);
}
