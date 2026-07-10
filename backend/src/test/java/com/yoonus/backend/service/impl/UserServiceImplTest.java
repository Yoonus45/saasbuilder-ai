package com.yoonus.backend.service.impl;

import com.yoonus.backend.dto.RegisterRequest;
import com.yoonus.backend.entity.User;
import com.yoonus.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

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
    void register_shouldHashPasswordAndAssignDefaultRole() {
        RegisterRequest request = new RegisterRequest();
        request.setName("Alice");
        request.setEmail("alice@example.com");
        request.setPassword("secret123");

        when(userRepository.existsByEmail("alice@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("encoded-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        User savedUser = userService.register(request);

        assertNotNull(savedUser);
        assertEquals("encoded-password", savedUser.getPassword());
        assertEquals("ROLE_USER", savedUser.getRole());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void login_shouldThrowWhenPasswordDoesNotMatch() {
        User user = new User();
        user.setEmail("alice@example.com");
        user.setPassword("encoded-password");

        when(userRepository.findByEmail("alice@example.com")).thenReturn(java.util.Optional.of(user));
        when(passwordEncoder.matches("wrong", "encoded-password")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> userService.login("alice@example.com", "wrong"));
    }
}
