package com.yoonus.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * JPA entity representing an application user.
 * Passwords are stored as BCrypt hashes — never in plain text.
 */
@Entity
@Table(name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = "email"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Full display name of the user. */
    @Column(nullable = false)
    private String name;

    /** Unique email — used as the authentication principal. */
    @Column(nullable = false, unique = true)
    private String email;

    /** BCrypt-hashed password. */
    @Column(nullable = false)
    private String password;

    /**
     * Role of the user (e.g. "USER", "ADMIN").
     * Defaults to "USER" on creation.
     */
    @Column(nullable = false)
    @Builder.Default
    private String role = "USER";

    /** Timestamp when the account was created (UTC). */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Automatically set createdAt before first persist. */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
