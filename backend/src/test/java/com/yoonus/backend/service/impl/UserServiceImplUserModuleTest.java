package com.yoonus.backend.service.impl;

import com.yoonus.backend.dto.UpdateProfileRequest;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplUserModuleTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getCurrentUser_shouldReturnUserByEmail() {
        User user = new User();
        user.setEmail("alice@example.com");
        user.setName("Alice");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        User result = userService.getCurrentUser("alice@example.com");

        assertEquals("Alice", result.getName());
    }

    @Test
    void updateProfile_shouldPersistUpdatedValues() {
        User user = new User();
        user.setEmail("old@example.com");
        user.setName("Old Name");

        when(userRepository.findByEmail("old@example.com")).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("new@example.com")).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User updated = userService.updateProfile("old@example.com", new UpdateProfileRequest("New Name", "new@example.com"));

        assertEquals("New Name", updated.getName());
        assertEquals("new@example.com", updated.getEmail());
        verify(userRepository).save(user);
    }

    @Test
    void deleteAccount_shouldRemoveUser() {
        User user = new User();
        user.setEmail("alice@example.com");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(Optional.of(user));

        userService.deleteAccount("alice@example.com");

        verify(userRepository).delete(user);
    }
}
