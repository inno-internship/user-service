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
import com.innowise.userservice.application.service.CacheService;
import com.innowise.userservice.infrastructure.cache.CacheKeys;
import com.innowise.userservice.application.dto.cache.UserCacheDto;
import com.innowise.userservice.application.mapper.CacheMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final CacheService cacheService;
    private final CacheMapper cacheMapper;

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

        cacheService.evict(CacheKeys.getUserWithCardsKey(user.getId()));
        log.info("Evicted user with id: {}", user.getId());
        
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

        cacheService.evict(CacheKeys.getUserWithCardsKey(id));
        log.info("Evicted user and user cards with user id: {}", id);
    }

    @Override
    public UserResponse getUserById(UUID id) {
        log.debug("Fetching user with id: {}", id);

        UserCacheDto cachedUser = cacheService.get(CacheKeys.getUserWithCardsKey(id), UserCacheDto.class);
        
        if (cachedUser != null) {
            log.debug("Found user in cache with id: {}", id);
            User user = cacheMapper.toUser(cachedUser);
            return userMapper.toResponse(user);
        }

        log.debug("User not found in cache with id: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found with id: {}", id);
                    return new UserNotFoundException("User not found with id: " + id);
                });

        cacheService.put(CacheKeys.getUserWithCardsKey(id), cacheMapper.toUserCacheDto(user));
        log.debug("Cached user and cards with id: {}", id);

        return userMapper.toResponse(user);
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

        final List<UUID> uniqueIds = ids.stream().distinct().toList();

        final Map<UUID, User> foundUsers = fetchFromCache(uniqueIds);

        final List<UUID> missingIds = uniqueIds.stream()
                .filter(id -> !foundUsers.containsKey(id))
                .toList();

        if (!missingIds.isEmpty()) {
            resolveMissingUsers(missingIds, foundUsers);
        }

        List<UserResponse> responses = uniqueIds.stream()
                .map(foundUsers::get)
                .map(userMapper::toResponse)
                .toList();
        
        log.debug("Found {} users", responses.size());
        return responses;
    }

    private Map<UUID, User> fetchFromCache(List<UUID> ids) {
        final List<String> keys = ids.stream().map(CacheKeys::getUserWithCardsKey).toList();
        final List<UserCacheDto> cachedDtos = cacheService.multiGet(keys, UserCacheDto.class);

        final Map<UUID, User> cachedUsers = new HashMap<>();
        for (int i = 0; i < ids.size(); i++) {
            UserCacheDto dto = cachedDtos.get(i);
            if (dto != null) {
                cachedUsers.put(ids.get(i), cacheMapper.toUser(dto));
            }
        }
        return cachedUsers;
    }

    private void resolveMissingUsers(List<UUID> idsToFetch, Map<UUID, User> foundUsers) {
        log.debug("Cache miss for user ids: {}. Fetching from DB.", idsToFetch);
        final List<User> usersFromDb = userRepository.findAllByIdIn(idsToFetch);

        if (usersFromDb.size() != idsToFetch.size()) {
            final Set<UUID> foundDbIds = usersFromDb.stream()
                    .map(User::getId)
                    .collect(Collectors.toSet());
            final List<UUID> notFoundIds = idsToFetch.stream()
                    .filter(id -> !foundDbIds.contains(id))
                    .toList();
            log.warn("Users not found with ids: {}", notFoundIds);
            throw new UserNotFoundException("Users not found with ids: " + notFoundIds);
        }

        usersFromDb.forEach(user -> {
            foundUsers.put(user.getId(), user);
            cacheService.put(CacheKeys.getUserWithCardsKey(user.getId()), cacheMapper.toUserCacheDto(user));
        });
    }
} 