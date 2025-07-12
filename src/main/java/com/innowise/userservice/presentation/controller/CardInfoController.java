package com.innowise.userservice.presentation.controller;

import com.innowise.userservice.application.dto.request.CreateCardInfoRequest;
import com.innowise.userservice.application.dto.response.CardInfoResponse;
import com.innowise.userservice.application.service.CardInfoService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller for managing card information operations.
 * 
 * Provides endpoints for creating, retrieving, and deleting card information records.
 *
 * @since 1.0
 */
@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardInfoController {

    private final CardInfoService cardInfoService;

    /**
     * Creates a new card information record.
     *
     * @param request the card information details as {@link CreateCardInfoRequest}
     * @return the created card information as {@link CardInfoResponse}
     *
     * Possible responses:
     * <ul>
     *   <li>201 Created — card info successfully created</li>
     *   <li>400 Bad Request — invalid input data</li>
     *   <li>404 Not Found — user not found</li>
     *   <li>500 Internal Server Error — unexpected error</li>
     * </ul>
     */
    @PostMapping
    public ResponseEntity<CardInfoResponse> createCardInfo(@RequestBody @Valid CreateCardInfoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).
                body(cardInfoService.createCardInfo(request));
    }

    /**
     * Deletes card information by its unique identifier.
     *
     * @param id unique identifier of the card information to delete
     * @return HTTP 204 No Content if deletion is successful
     *
     * Possible responses:
     * <ul>
     *   <li>204 No Content — card info successfully deleted</li>
     *   <li>404 Not Found — card info not found</li>
     *   <li>500 Internal Server Error — unexpected error</li>
     * </ul>
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCardInfo(@PathVariable @NotNull UUID id) {
        cardInfoService.deleteCardInfo(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Retrieves card information by its unique identifier.
     *
     * @param id unique identifier of the card information to retrieve
     * @return the card information as {@link CardInfoResponse}
     *
     * Possible responses:
     * <ul>
     *   <li>200 OK — card info found</li>
     *   <li>404 Not Found — card info not found</li>
     *   <li>500 Internal Server Error — unexpected error</li>
     * </ul>
     */
    @GetMapping("/{id}")
    public ResponseEntity<CardInfoResponse> getCardInfoById(@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(cardInfoService.getCardInfoById(id));
    }

    /**
     * Retrieves all card information records associated with a specific user.
     *
     * @param userId unique identifier of the user whose cards to retrieve
     * @return list of card information as {@link CardInfoResponse}
     *
     * Possible responses:
     * <ul>
     *   <li>200 OK — cards found</li>
     *   <li>404 Not Found — user not found</li>
     *   <li>500 Internal Server Error — unexpected error</li>
     * </ul>
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CardInfoResponse>> getCardInfoByUserId(@PathVariable @NotNull UUID userId) {
        return ResponseEntity.ok(cardInfoService.getCardInfoByUserId(userId));
    }
}