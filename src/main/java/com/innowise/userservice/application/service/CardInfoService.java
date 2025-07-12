package com.innowise.userservice.application.service;

import com.innowise.userservice.application.dto.request.CreateCardInfoRequest;
import com.innowise.userservice.application.dto.response.CardInfoResponse;
import com.innowise.userservice.application.exception.notfound.CardNotFoundException;
import com.innowise.userservice.application.exception.notfound.UserNotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing card information operations.
 * Provides functionality for creating, retrieving, and deleting card information.
 *
 * @since 1.0
 */
public interface CardInfoService {
    /**
     * Creates a new card information record.
     *
     * @param request the card information details for creation
     * @return the created card information as {@link CardInfoResponse}
     * @throws UserNotFoundException if the associated user is not found
     */
    CardInfoResponse createCardInfo(CreateCardInfoRequest request);

    /**
     * Deletes card information by its identifier.
     *
     * @param id unique identifier of the card information to delete
     * @throws CardNotFoundException if card with given id is not found
     */
    void deleteCardInfo(UUID id);

    /**
     * Retrieves card information by its identifier.
     *
     * @param id unique identifier of the card information to retrieve
     * @return the card information as {@link CardInfoResponse}
     * @throws CardNotFoundException if card with given id is not found
     */
    CardInfoResponse getCardInfoById(UUID id);

    /**
     * Retrieves all card information associated with a specific user.
     *
     * @param userId unique identifier of the user whose cards to retrieve
     * @return list of card information as {@link CardInfoResponse}
     * @throws UserNotFoundException if user with given id is not found
     */
    List<CardInfoResponse> getCardInfoByUserId(UUID userId);
} 