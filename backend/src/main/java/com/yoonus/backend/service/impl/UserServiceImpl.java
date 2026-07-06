package com.yoonus.backend.service.impl;

import com.yoonus.backend.dto.RegisterRequest;
import com.yoonus.backend.dto.UpdateProfileRequest;
import com.yoonus.backend.entity.Role;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.exception.DuplicateResourceException;
import com.yoonus.backend.exception.InvalidCredentialsException;
import com.yoonus.backend.exception.ResourceNotFoundException;
import com.yoonus.backend.repository.AiGenerationHistoryRepository;
import com.yoonus.backend.repository.PasswordResetTokenRepository;
import com.yoonus.backend.repository.ProjectRepository;
import com.yoonus.backend.repository.SubscriptionRepository;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.security.JwtUtil;
import com.yoonus.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ProjectRepository projectRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AiGenerationHistoryRepository aiGenerationHistoryRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JwtUtil jwtUtil;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           ProjectRepository projectRepository,
                           SubscriptionRepository subscriptionRepository,
                           AiGenerationHistoryRepository aiGenerationHistoryRepository,
                           PasswordResetTokenRepository passwordResetTokenRepository,
                           JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.projectRepository = projectRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.aiGenerationHistoryRepository = aiGenerationHistoryRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public User register(RegisterRequest request) {
        logger.info("===== REGISTRATION FLOW START =====");
        logger.info("Incoming RegisterRequest - name: {}, email: {}, password length: {}", 
            request.getName(), request.getEmail(), request.getPassword() != null ? request.getPassword().length() : 0);
        
        if (userRepository.existsByEmail(request.getEmail())) {
            logger.warn("Registration failed: Email {} already exists", request.getEmail());
            throw new DuplicateResourceException("Email already exists");
        }
        logger.info("Email {} is unique, proceeding", request.getEmail());

        User user = new User();
        user.setName(request.getName());
        logger.info("Set user name: {}", user.getName());
        
        user.setEmail(request.getEmail().trim().toLowerCase());
        logger.info("Set user email (normalized): {}", user.getEmail());
        
        logger.info("Encoding password...");
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        logger.info("Password encoded successfully");
        
        user.setRole(Role.ROLE_USER);
        logger.info("Set user role: {}", user.getRole());
        logger.info("Attempting to save user to database...");

        try {
            User savedUser = userRepository.save(user);
            logger.info("User saved successfully, ID: {}, Email: {}", savedUser.getId(), savedUser.getEmail());
            logger.info("===== REGISTRATION FLOW END =====");
            return savedUser;
        } catch (Exception e) {
            logger.error("Database save failed: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        return user;
    }

    @Override
    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public User getCurrentUserFromToken(String token) {
        String email = jwtUtil.extractEmail(token);
        if (email == null || email.isBlank()) {
            throw new ResourceNotFoundException("Invalid token");
        }
        return getCurrentUser(email);
    }

    @Override
    public User updateProfile(String email, UpdateProfileRequest request) {
        User user = getCurrentUser(email);

        if (!user.getEmail().equals(request.getEmail().trim().toLowerCase())
                && userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new DuplicateResourceException("Email already exists");
        }

        user.setName(request.getName());
        user.setEmail(request.getEmail().trim().toLowerCase());
        return userRepository.save(user);
    }

    @Override
    public void deleteAccount(String email) {
        User user = getCurrentUser(email);

        projectRepository.deleteAll(projectRepository.findAllByUser(user));
        subscriptionRepository.deleteAll(subscriptionRepository.findAllByUser(user));
        aiGenerationHistoryRepository.deleteAll(aiGenerationHistoryRepository.findAllByUser(user));
        passwordResetTokenRepository.deleteAll(passwordResetTokenRepository.findAllByUser(user));

        userRepository.delete(user);
    }
}