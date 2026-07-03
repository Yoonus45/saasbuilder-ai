package com.yoonus.backend.repository;

import com.yoonus.backend.entity.PasswordResetToken;
import com.yoonus.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    Optional<PasswordResetToken> findByUserAndUsedFalse(User user);
    java.util.List<PasswordResetToken> findAllByUser(User user);
}
