package com.innowise.userservice.application.service;

import com.innowise.userservice.application.dto.request.CreateUserRequest;
import com.innowise.userservice.application.dto.request.UpdateUserRequest;
import com.innowise.userservice.application.dto.response.UserResponse;
import com.innowise.userservice.application.exception.alreadyexists.UserAlreadyExists;
import com.innowise.userservice.application.exception.notfound.UserNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing user operations.
 * Provides functionality for creating, updating, retrieving, and deleting users.
 *
 * @since 1.0
 */
public interface UserService {
    /**
     * Creates a new user.
     *
     * @param request the user details for creation
     * @return the created user as {@link UserResponse}
     * @throws UserAlreadyExists if user with given email already exists
     */
    UserResponse createUser(CreateUserRequest request);

    /**
     * Updates an existing user's information.
     *
     * @param id unique identifier of the user to update
     * @param request the updated user details
     * @return the updated user as {@link UserResponse}
     * @throws UserNotFoundException if user with given id is not found
     * @throws UserAlreadyExists if updated email already exists for another user
     */
    UserResponse updateUser(UUID id, UpdateUserRequest request);

    /**
     * Deletes a user by their identifier.
     *
     * @param id unique identifier of the user to delete
     * @throws UserNotFoundException if user with given id is not found
     */
    void deleteUser(UUID id);

    /**
     * Retrieves a user by their identifier.
     *
     * @param id unique identifier of the user to retrieve
     * @return the user as {@link UserResponse}
     * @throws UserNotFoundException if user with given id is not found
     */
    UserResponse getUserById(UUID id);

    /**
     * Retrieves all users in the system.
     *
     * @return list of all users as {@link UserResponse}
     */
    List<UserResponse> getAllUsers();

    /**
     * Retrieves a user by their email address.
     *
     * @param email email address of the user to retrieve
     * @return the user as {@link UserResponse}
     * @throws UserNotFoundException if user with given email is not found
     */
    UserResponse getUserByEmail(String email);

    /**
     * Retrieves multiple users by their identifiers.
     *
     * @param ids list of unique identifiers of users to retrieve
     * @return list of found users as {@link UserResponse}
     */
    List<UserResponse> getAllUsersByIds(List<UUID> ids);
} 