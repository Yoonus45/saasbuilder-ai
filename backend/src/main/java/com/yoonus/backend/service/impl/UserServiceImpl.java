package com.yoonus.backend.service.impl;

import com.yoonus.backend.dto.RegisterRequest;
import com.yoonus.backend.dto.UpdateProfileRequest;
import com.yoonus.backend.entity.Role;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.exception.DuplicateResourceException;
import com.yoonus.backend.exception.InvalidCredentialsException;
import com.yoonus.backend.exception.ResourceNotFoundException;
import com.yoonus.backend.repository.UserRepository;
import com.yoonus.backend.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);

        return userRepository.save(user);
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
        userRepository.delete(user);
    }
}