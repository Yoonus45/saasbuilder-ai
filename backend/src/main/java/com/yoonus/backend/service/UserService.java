package com.yoonus.backend.service;

import com.yoonus.backend.dto.RegisterRequest;
import com.yoonus.backend.dto.UpdateProfileRequest;
import com.yoonus.backend.entity.User;

public interface UserService {

    User register(RegisterRequest request);

    User login(String email, String password);

    User getCurrentUser(String email);

    User getCurrentUserFromToken(String token);

    User updateProfile(String email, UpdateProfileRequest request);

    void deleteAccount(String email);

}