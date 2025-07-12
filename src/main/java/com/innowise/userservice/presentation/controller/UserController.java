package com.innowise.userservice.presentation.controller;

import com.innowise.userservice.application.dto.request.CreateUserRequest;
import com.innowise.userservice.application.dto.request.UpdateUserRequest;
import com.innowise.userservice.application.dto.response.UserResponse;
import com.innowise.userservice.application.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing user operations.
 * 
 * Provides endpoints for creating, updating, retrieving, and deleting users.
 *
 * @since 1.0
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Creates a new user with the provided details.
     *
     * @param request the user creation details as {@link CreateUserRequest}
     * @return the created user as {@link UserResponse} 
     *
     * Possible responses:
     * <ul>
     *   <li>201 Created — user successfully created</li>
     *   <li>400 Bad Request — invalid input data</li>
     *   <li>409 Conflict — user with given email already exists</li>
     *   <li>500 Internal Server Error — unexpected error</li>
     * </ul>
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    /**
     * Updates an existing user's information.
     *
     * @param id unique identifier of the user to update
     * @param request the updated user details as {@link UpdateUserRequest}
     * @return the updated user as {@link UserResponse}
     *
     * Possible responses:
     * <ul>
     *   <li>200 OK — user successfully updated</li>
     *   <li>400 Bad Request — invalid input data</li>
     *   <li>404 Not Found — user not found</li>
     *   <li>409 Conflict — updated email already exists for another user</li>
     *   <li>500 Internal Server Error — unexpected error</li>
     * </ul>
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable @NotNull UUID id, @RequestBody @Valid UpdateUserRequest request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    /**
     * Deletes a user by their unique identifier.
     *
     * @param id unique identifier of the user to delete
     * @return HTTP 204 No Content if deletion is successful
     *
     * Possible responses:
     * <ul>
     *   <li>204 No Content — user successfully deleted</li>
     *   <li>404 Not Found — user not found</li>
     *   <li>500 Internal Server Error — unexpected error</li>
     * </ul>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable @NotNull UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves a user by their unique identifier.
     *
     * @param id unique identifier of the user to retrieve
     * @return the user as {@link UserResponse}
     *
     * Possible responses:
     * <ul>
     *   <li>200 OK — user found</li>
     *   <li>404 Not Found — user not found</li>
     *   <li>500 Internal Server Error — unexpected error</li>
     * </ul>
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Retrieves all users in the system.
     *
     * @return list of all users as {@link UserResponse}
     *
     * Possible responses:
     * <ul>
     *   <li>200 OK — users found</li>
     *   <li>500 Internal Server Error — unexpected error</li>
     * </ul>
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Retrieves a user by their email address.
     *
     * @param email email address of the user to retrieve
     * @return the user as {@link UserResponse}
     *
     * Possible responses:
     * <ul>
     *   <li>200 OK — user found</li>
     *   <li>404 Not Found — user not found</li>
     *   <li>500 Internal Server Error — unexpected error</li>
     * </ul>
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    /**
     * Retrieves multiple users by their unique identifiers.
     *
     * @param ids list of unique identifiers of users to retrieve
     * @return list of found users as {@link UserResponse}
     *
     * Possible responses:
     * <ul>
     *   <li>200 OK — users found</li>
     *   <li>404 Not Found — one or more users not found</li>
     *   <li>500 Internal Server Error — unexpected error</li>
     * </ul>
     */
    @GetMapping("/by-ids")
    public ResponseEntity<List<UserResponse>> getAllUsersByIds(@RequestParam @NotNull List<UUID> ids) {
        return ResponseEntity.ok(userService.getAllUsersByIds(ids));
    }
} 