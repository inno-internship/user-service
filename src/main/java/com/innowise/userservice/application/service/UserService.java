package com.innowise.userservice.application.service;

import com.innowise.userservice.application.dto.request.CreateUserRequest;
import com.innowise.userservice.application.dto.request.UpdateUserRequest;
import com.innowise.userservice.application.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

public interface UserService {
    UserResponse createUser(CreateUserRequest request);
    UserResponse updateUser(UUID id, UpdateUserRequest request);
    void deleteUser(UUID id);
    UserResponse getUserById(UUID id);
    List<UserResponse> getAllUsers();
    UserResponse getUserByEmail(String email);
    List<UserResponse> getAllUsersByIds(List<UUID> ids);
} 