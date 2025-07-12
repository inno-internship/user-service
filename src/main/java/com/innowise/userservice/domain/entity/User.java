package com.innowise.userservice.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.CascadeType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Entity representing a user in the system.
 * <p>
 * Contains personal information about the user and maintains
 * a relationship with their credit cards. All modifications to
 * associated cards are cascaded.
 *
 * @since 1.0
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    /**
     * Unique identifier for the user.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The user's first name.
     * Must not exceed 100 characters and not be null.
     */
    @Column(nullable = false, length = 100)
    private String name;

    /**
     * The user's last name.
     * Must not exceed 100 characters and not be null.
     */
    @Column(nullable = false, length = 100)
    private String surname;

    /**
     * The user's date of birth.
     * Must not be null.
     */
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    /**
     * The user's email address.
     * Must be unique across all users, not exceed 255 characters,
     * and not be null.
     */
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    /**
     * The user's associated credit cards.
     * <p>Each card belongs to this user.
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CardInfo> cards;
}
