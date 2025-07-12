package com.innowise.userservice.domain.repository;

import com.innowise.userservice.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing {@link User} entities.
 * Provides JPA operations and custom queries for user management.
 *
 * @since 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    /**
     * Retrieves a user by their email address.
     *
     * @param email email address of the user to retrieve
     * @return an {@link Optional} containing the found user or empty if not found
     */
    Optional<User> findByEmail(String email);

    /**
     * Retrieves multiple users by their identifiers.
     *
     * @param ids list of unique identifiers of users to retrieve
     * @return list of found users
     */
    List<User> findAllByIdIn(List<UUID> ids);

    /**
     * Retrieves all users with their associated cards eagerly loaded.
     * Uses LEFT JOIN FETCH to avoid N+1 query problem.
     *
     * @return list of all users with their cards
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.cards")
    List<User> findAllWithCards();
    
    /**
     * Checks if a user with the given email exists.
     *
     * @param email email address to check
     * @return true if a user with the email exists, false otherwise
     */
    boolean existsByEmail(String email);
}
