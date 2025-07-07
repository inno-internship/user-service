package com.innowise.userservice.application.service;

import com.innowise.userservice.application.dto.request.CreateCardInfoRequest;
import com.innowise.userservice.application.dto.response.CardInfoResponse;

import java.util.List;
import java.util.UUID;

public interface CardInfoService {
    CardInfoResponse createCardInfo(CreateCardInfoRequest request);
    void deleteCardInfo(UUID id);
    CardInfoResponse getCardInfoById(UUID id);
    List<CardInfoResponse> getCardInfoByUserId(UUID userId);
} 