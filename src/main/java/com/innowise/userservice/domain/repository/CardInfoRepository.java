package com.innowise.userservice.domain.repository;

import com.innowise.userservice.domain.entity.CardInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for managing {@link CardInfo} entities.
 * Provides JPA operations and custom queries for card information.
 *
 * @since 1.0
 */
@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, UUID> {

    /**
     * Retrieves a card by its identifier using JPQL query.
     *
     * @param id unique identifier of the card to retrieve
     * @return an {@link Optional} containing the found card or empty if not found
     */
    @Query("SELECT c FROM CardInfo c WHERE c.id = :id")
    Optional<CardInfo> findByIdJPQL(@Param("id") UUID id);

    /**
     * Retrieves multiple cards by their identifiers using native SQL query.
     *
     * @param ids list of unique identifiers of cards to retrieve
     * @return list of found cards
     */
    @Query(value = "SELECT * FROM card_info WHERE id IN (:ids)", nativeQuery = true)
    List<CardInfo> findAllByIdsNative(@Param("ids") List<UUID> ids);

    /**
     * Retrieves all cards associated with a specific user.
     *
     * @param userId unique identifier of the user whose cards to retrieve
     * @return list of cards belonging to the specified user
     */
    List<CardInfo> findByUserId(UUID userId);
}
