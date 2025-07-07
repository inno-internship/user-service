package com.innowise.userservice.application.service.impl;

import com.innowise.userservice.application.dto.request.CreateUserRequest;
import com.innowise.userservice.application.dto.request.UpdateUserRequest;
import com.innowise.userservice.application.dto.response.UserResponse;
import com.innowise.userservice.application.mapper.UserMapper;
import com.innowise.userservice.application.service.UserService;
import com.innowise.userservice.domain.entity.User;
import com.innowise.userservice.domain.repository.UserRepository;
import com.innowise.userservice.application.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserResponse createUser(CreateUserRequest request) {
        User user = userMapper.toEntity(request);
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
        
        userMapper.updateEntity(user, request);
        user = userRepository.save(user);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    public UserResponse getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
    }

    @Override
    public List<UserResponse> getAllUsersByIds(List<UUID> ids) {
        List<UUID> uniqueIds = ids.stream().distinct().toList();
        List<User> users = userRepository.findAllByIdIn(uniqueIds);

        if (users.size() != uniqueIds.size()) {
            List<UUID> foundIds = users.stream().map(User::getId).toList();
            List<UUID> notFoundIds = uniqueIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .toList();
            throw new UserNotFoundException(notFoundIds);
        }
        
        return users.stream().map(userMapper::toResponse).toList();
    }
} 