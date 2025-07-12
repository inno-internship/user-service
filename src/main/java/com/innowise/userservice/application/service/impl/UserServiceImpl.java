package com.innowise.userservice.application.service.impl;

import com.innowise.userservice.application.dto.request.CreateUserRequest;
import com.innowise.userservice.application.dto.request.UpdateUserRequest;
import com.innowise.userservice.application.dto.response.UserResponse;
import com.innowise.userservice.application.mapper.UserMapper;
import com.innowise.userservice.application.service.UserService;
import com.innowise.userservice.domain.entity.User;
import com.innowise.userservice.domain.repository.UserRepository;
import com.innowise.userservice.application.exception.notfound.UserNotFoundException;
import com.innowise.userservice.application.exception.alreadyexists.UserAlreadyExists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        log.debug("Creating user with email: {}", request.email());

        if (userRepository.existsByEmail(request.email())) {
            log.warn("Attempt to create user with existing email: {}", request.email());
            throw new UserAlreadyExists("User with email " + request.email() + " already exists");
        }
        
        User user = userMapper.toEntity(request);
        user = userRepository.save(user);
        log.info("Created new user with id: {}", user.getId());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        log.debug("Updating user with id: {}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attempt to update non-existing user with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });
        
        if (request.email() != null && !request.email().equals(user.getEmail()) && 
            userRepository.existsByEmail(request.email())) {
            log.warn("Attempt to update user with existing email: {}", request.email());
            throw new UserAlreadyExists("User with email " + request.email() + " already exists");
        }
        
        userMapper.updateEntity(user, request);
        user = userRepository.save(user);
        log.info("Updated user with id: {}", user.getId());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        log.debug("Deleting user with id: {}", id);

        if (!userRepository.existsById(id)) {
            log.warn("Attempt to delete non-existing user with id: {}", id);
            throw new UserNotFoundException("User not found with id: " + id);
        }

        userRepository.deleteById(id);
        log.info("Deleted user with id: {}", id);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        log.debug("Fetching user with id: {}", id);

        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });
    }

    @Override
    public List<UserResponse> getAllUsers() {
        log.debug("Fetching all users");

        List<UserResponse> users = userRepository.findAllWithCards().stream()
                .map(userMapper::toResponse)
                .toList();

        log.debug("Found {} users", users.size());
        return users;
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        log.debug("Fetching user with email: {}", email);

        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", email);
                    return new UserNotFoundException("User not found with email: " + email);
                });
    }

    @Override
    public List<UserResponse> getAllUsersByIds(List<UUID> ids) {
        log.debug("Fetching users with ids: {}", ids);
        
        List<UUID> uniqueIds = ids.stream().distinct().toList();
        List<User> users = userRepository.findAllByIdIn(uniqueIds);

        if (users.size() != uniqueIds.size()) {
            List<UUID> foundIds = users.stream().map(User::getId).toList();
            List<UUID> notFoundIds = uniqueIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            log.warn("Users not found with ids: {}", notFoundIds);
            throw new UserNotFoundException("User not found with ids: " + notFoundIds);
        }
        
        log.debug("Found {} users", users.size());
        return users.stream().map(userMapper::toResponse).toList();
    }
} 