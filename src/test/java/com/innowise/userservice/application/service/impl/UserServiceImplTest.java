package com.innowise.userservice.application.service.impl;

import com.innowise.userservice.application.dto.request.CreateUserRequest;
import com.innowise.userservice.application.dto.response.UserResponse;
import com.innowise.userservice.application.exception.alreadyexists.UserAlreadyExists;
import com.innowise.userservice.application.mapper.CacheMapper;
import com.innowise.userservice.application.mapper.UserMapper;
import com.innowise.userservice.application.service.CacheService;
import com.innowise.userservice.domain.entity.User;
import com.innowise.userservice.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;

import com.innowise.userservice.application.dto.cache.UserCacheDto;
import com.innowise.userservice.application.dto.request.UpdateUserRequest;
import com.innowise.userservice.application.exception.notfound.UserNotFoundException;
import java.util.Optional;
import com.innowise.userservice.infrastructure.cache.CacheKeys;
import java.util.List;
import java.util.Collections;
import java.util.Arrays;
import java.util.stream.Collectors;


@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CacheService cacheService;

    @Mock
    private CacheMapper cacheMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private CreateUserRequest createUserRequest;
    private UpdateUserRequest updateUserRequest;
    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        createUserRequest = new CreateUserRequest("John", "Doe", LocalDate.of(1990, 5, 15), "john.doe@example.com");
        updateUserRequest = new UpdateUserRequest("Jane", "Doe", null, "jane.doe@example.com");
        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("john.doe@example.com");
        userResponse = new UserResponse(user.getId(), "Jane", "Doe", LocalDate.of(1990, 5, 15), "jane.doe@example.com", new ArrayList<>());
    }

    @Test
    @DisplayName("Test create user successfully")
    void createUser_whenEmailNotExists_shouldCreateUser() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userMapper.toEntity(any(CreateUserRequest.class))).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        UserResponse result = userService.createUser(createUserRequest);

        assertNotNull(result);
        assertEquals(userResponse, result);
        assertEquals(userResponse.id(), result.id());

        verify(userRepository).existsByEmail(createUserRequest.email());
        verify(userMapper).toEntity(createUserRequest);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Test create user throws UserAlreadyExists")
    void createUser_whenEmailExists_shouldThrowUserAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(UserAlreadyExists.class, () -> {
            userService.createUser(createUserRequest);
        });

        verify(userRepository).existsByEmail(createUserRequest.email());
        verify(userMapper, never()).toEntity(any(CreateUserRequest.class));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Test update user successfully")
    void updateUser_whenUserExists_shouldUpdateUser() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);
        doNothing().when(cacheService).evict(anyString());

        UserResponse result = userService.updateUser(user.getId(), updateUserRequest);

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userRepository).findById(user.getId());
        verify(userRepository).existsByEmail(updateUserRequest.email());
        verify(userMapper).updateEntity(user, updateUserRequest);
        verify(userRepository).save(user);
        verify(userMapper).toResponse(user);
        verify(cacheService).evict(CacheKeys.getUserWithCardsKey(user.getId()));
    }

    @Test
    @DisplayName("Test update user throws UserNotFoundException")
    void updateUser_whenUserNotFound_shouldThrowUserNotFoundException() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(user.getId(), updateUserRequest);
        });

        verify(userRepository).findById(user.getId());
        verify(userRepository, never()).existsByEmail(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Test update user throws UserAlreadyExists")
    void updateUser_whenEmailExists_shouldThrowUserAlreadyExists() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(UserAlreadyExists.class, () -> {
            userService.updateUser(user.getId(), updateUserRequest);
        });

        verify(userRepository).findById(user.getId());
        verify(userRepository).existsByEmail(updateUserRequest.email());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Test delete user successfully")
    void deleteUser_whenUserExists_shouldDeleteUser() {
        when(userRepository.existsById(any(UUID.class))).thenReturn(true);
        doNothing().when(userRepository).deleteById(any(UUID.class));
        doNothing().when(cacheService).evict(anyString());

        userService.deleteUser(user.getId());

        verify(userRepository).existsById(user.getId());
        verify(userRepository).deleteById(user.getId());
        verify(cacheService).evict(CacheKeys.getUserWithCardsKey(user.getId()));
    }

    @Test
    @DisplayName("Test delete user throws UserNotFoundException")
    void deleteUser_whenUserNotFound_shouldThrowUserNotFoundException() {
        when(userRepository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(user.getId());
        });

        verify(userRepository).existsById(user.getId());
        verify(userRepository, never()).deleteById(any(UUID.class));
        verify(cacheService, never()).evict(anyString());
    }

    @Test
    @DisplayName("Test get user by id from cache")
    void getUserById_whenUserInCache_shouldReturnUserFromCache() {
        UserCacheDto cachedUserDto = new UserCacheDto();
        when(cacheService.get(anyString(), any(Class.class))).thenReturn(cachedUserDto);
        when(cacheMapper.toUser(any(UserCacheDto.class))).thenReturn(user);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        UserResponse result = userService.getUserById(user.getId());

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(cacheService).get(CacheKeys.getUserWithCardsKey(user.getId()), UserCacheDto.class);
        verify(cacheMapper).toUser(cachedUserDto);
        verify(userMapper).toResponse(user);
        verify(userRepository, never()).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Test get user by id from DB")
    void getUserById_whenUserNotInCache_shouldReturnUserFromDb() {
        when(cacheService.get(anyString(), any(Class.class))).thenReturn(null);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);
        UserCacheDto userCacheDto = new UserCacheDto();
        when(cacheMapper.toUserCacheDto(any(User.class))).thenReturn(userCacheDto);
        doNothing().when(cacheService).put(anyString(), any());

        UserResponse result = userService.getUserById(user.getId());

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(cacheService).get(CacheKeys.getUserWithCardsKey(user.getId()), UserCacheDto.class);
        verify(userRepository).findById(user.getId());
        verify(cacheMapper).toUserCacheDto(user);
        verify(cacheService).put(CacheKeys.getUserWithCardsKey(user.getId()), userCacheDto);
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Test get user by id throws UserNotFoundException")
    void getUserById_whenUserNotFound_shouldThrowUserNotFoundException() {
        when(cacheService.get(anyString(), any(Class.class))).thenReturn(null);
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(user.getId());
        });

        verify(cacheService).get(CacheKeys.getUserWithCardsKey(user.getId()), UserCacheDto.class);
        verify(userRepository).findById(user.getId());
        verify(cacheService, never()).put(anyString(), any());
    }

    @Test
    @DisplayName("Test get all users")
    void getAllUsers_shouldReturnListOfUsers() {
        when(userRepository.findAllWithCards()).thenReturn(Collections.singletonList(user));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        List<UserResponse> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(userResponse, result.get(0));

        verify(userRepository).findAllWithCards();
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Test get user by email successfully")
    void getUserByEmail_whenUserExists_shouldReturnUser() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse);

        UserResponse result = userService.getUserByEmail(user.getEmail());

        assertNotNull(result);
        assertEquals(userResponse, result);

        verify(userRepository).findByEmail(user.getEmail());
        verify(userMapper).toResponse(user);
    }

    @Test
    @DisplayName("Test get user by email throws UserNotFoundException")
    void getUserByEmail_whenUserNotFound_shouldThrowUserNotFoundException() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByEmail(user.getEmail());
        });

        verify(userRepository).findByEmail(user.getEmail());
        verify(userMapper, never()).toResponse(any(User.class));
    }

    @Test
    @DisplayName("Test get all users by ids - all from cache")
    void getAllUsersByIds_whenAllUsersInCache_shouldReturnUsersFromCache() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        List<User> users = Arrays.asList(user, otherUser);
        List<UUID> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        List<String> cacheKeys = userIds.stream().map(CacheKeys::getUserWithCardsKey).collect(Collectors.toList());
        UserCacheDto userCacheDto1 = new UserCacheDto();
        UserCacheDto userCacheDto2 = new UserCacheDto();

        when(cacheService.multiGet(any(), any(Class.class))).thenReturn(Arrays.asList(userCacheDto1, userCacheDto2));
        when(cacheMapper.toUser(userCacheDto1)).thenReturn(user);
        when(cacheMapper.toUser(userCacheDto2)).thenReturn(otherUser);
        when(userMapper.toResponse(any(User.class))).thenReturn(userResponse, new UserResponse(otherUser.getId(), "Other", "User", null, "other@test.com", Collections.emptyList()));

        List<UserResponse> result = userService.getAllUsersByIds(userIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(cacheService).multiGet(cacheKeys, UserCacheDto.class);
        verify(userRepository, never()).findAllByIdIn(any());
    }

    @Test
    @DisplayName("Test get all users by ids - mixed cache and DB")
    void getAllUsersByIds_whenMixedCacheAndDb_shouldReturnCombinedUsers() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        List<User> users = Arrays.asList(user, otherUser);
        List<UUID> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        List<String> cacheKeys = userIds.stream().map(CacheKeys::getUserWithCardsKey).collect(Collectors.toList());
        UserCacheDto userCacheDto1 = new UserCacheDto();

        when(cacheService.multiGet(any(), any(Class.class))).thenReturn(Arrays.asList(userCacheDto1, null));
        when(cacheMapper.toUser(userCacheDto1)).thenReturn(user);

        when(userRepository.findAllByIdIn(Collections.singletonList(otherUser.getId()))).thenReturn(Collections.singletonList(otherUser));

        when(userMapper.toResponse(user)).thenReturn(userResponse);
        UserResponse otherUserResponse = new UserResponse(otherUser.getId(), "Other", "User", null, "other@test.com", Collections.emptyList());
        when(userMapper.toResponse(otherUser)).thenReturn(otherUserResponse);

        UserCacheDto otherUserCacheDto = new UserCacheDto();
        when(cacheMapper.toUserCacheDto(otherUser)).thenReturn(otherUserCacheDto);

        List<UserResponse> result = userService.getAllUsersByIds(userIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(cacheService).multiGet(cacheKeys, UserCacheDto.class);
        verify(userRepository).findAllByIdIn(Collections.singletonList(otherUser.getId()));
        verify(cacheService).put(CacheKeys.getUserWithCardsKey(otherUser.getId()), otherUserCacheDto);
    }

    @Test
    @DisplayName("Test get all users by ids - all from DB")
    void getAllUsersByIds_whenAllUsersInDb_shouldReturnUsersFromDb() {
        User otherUser = new User();
        otherUser.setId(UUID.randomUUID());
        List<User> users = Arrays.asList(user, otherUser);
        List<UUID> userIds = users.stream().map(User::getId).collect(Collectors.toList());
        List<String> cacheKeys = userIds.stream().map(CacheKeys::getUserWithCardsKey).collect(Collectors.toList());

        when(cacheService.multiGet(any(), any(Class.class))).thenReturn(Arrays.asList(null, null));
        when(userRepository.findAllByIdIn(userIds)).thenReturn(users);

        when(userMapper.toResponse(user)).thenReturn(userResponse);
        UserResponse otherUserResponse = new UserResponse(otherUser.getId(), "Other", "User", null, "other@test.com", Collections.emptyList());
        when(userMapper.toResponse(otherUser)).thenReturn(otherUserResponse);

        UserCacheDto userCacheDto = new UserCacheDto();
        when(cacheMapper.toUserCacheDto(any(User.class))).thenReturn(userCacheDto);

        List<UserResponse> result = userService.getAllUsersByIds(userIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(cacheService).multiGet(cacheKeys, UserCacheDto.class);
        verify(userRepository).findAllByIdIn(userIds);
        verify(cacheService).put(CacheKeys.getUserWithCardsKey(user.getId()), userCacheDto);
        verify(cacheService).put(CacheKeys.getUserWithCardsKey(otherUser.getId()), userCacheDto);
    }

    @Test
    @DisplayName("Test get all users by ids - UserNotFoundException")
    void getAllUsersByIds_whenUserNotFound_shouldThrowUserNotFoundException() {
        List<UUID> userIds = Collections.singletonList(UUID.randomUUID());
        List<String> cacheKeys = userIds.stream().map(CacheKeys::getUserWithCardsKey).collect(Collectors.toList());

        when(cacheService.multiGet(any(), any(Class.class))).thenReturn(Collections.singletonList(null));
        when(userRepository.findAllByIdIn(userIds)).thenReturn(Collections.emptyList());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getAllUsersByIds(userIds);
        });

        verify(cacheService).multiGet(cacheKeys, UserCacheDto.class);
        verify(userRepository).findAllByIdIn(userIds);
    }
} 