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

@RestController
@RequestMapping("/api/cards")
@RequiredArgsConstructor
public class CardInfoController {

    private final CardInfoService cardInfoService;

    @PostMapping
    public ResponseEntity<CardInfoResponse> createCardInfo(@RequestBody @Valid CreateCardInfoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).
                body(cardInfoService.createCardInfo(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCardInfo(@PathVariable @NotNull UUID id) {
        cardInfoService.deleteCardInfo(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CardInfoResponse> getCardInfoById(@PathVariable @NotNull UUID id) {
        return ResponseEntity.ok(cardInfoService.getCardInfoById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CardInfoResponse>> getCardInfoByUserId(@PathVariable @NotNull UUID userId) {
        return ResponseEntity.ok(cardInfoService.getCardInfoByUserId(userId));
    }
}