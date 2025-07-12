package com.innowise.userservice.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entity representing credit card information in the system.
 * <p>
 * Each card is associated with a specific user and contains
 * essential information about the credit card.
 *
 * @since 1.0
 */
@Entity
@Table(name = "card_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardInfo {

    /**
     * Unique identifier for the card information record.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * The user who owns this card.
     * Must not be null.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The card number.
     * Must be exactly 16 characters long and not null.
     */
    @Column(nullable = false, length = 16)
    private String number;

    /**
     * The name of the card holder as it appears on the card.
     * Must not exceed 100 characters and not be null.
     */
    @Column(nullable = false, length = 100)
    private String holder;

    /**
     * The date when the card expires.
     * Must not be null.
     */
    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;
}
