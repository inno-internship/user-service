package com.innowise.userservice.application.service.impl;

import com.innowise.userservice.application.dto.request.CreateCardInfoRequest;
import com.innowise.userservice.application.dto.response.CardInfoResponse;
import com.innowise.userservice.application.exception.notfound.CardNotFoundException;
import com.innowise.userservice.application.exception.notfound.UserNotFoundException;
import com.innowise.userservice.application.mapper.CardInfoMapper;
import com.innowise.userservice.application.service.CardInfoService;
import com.innowise.userservice.application.service.CacheService;
import com.innowise.userservice.domain.entity.CardInfo;
import com.innowise.userservice.domain.entity.User;
import com.innowise.userservice.domain.repository.CardInfoRepository;
import com.innowise.userservice.domain.repository.UserRepository;
import com.innowise.userservice.infrastructure.cache.CacheKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CardInfoServiceImpl implements CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final UserRepository userRepository;
    private final CardInfoMapper cardInfoMapper;
    private final CacheService cacheService;

    @Override
    @Transactional
    public CardInfoResponse createCardInfo(CreateCardInfoRequest request) {
        log.debug("Attempting to create card info for user with id: {}", request.userId());

        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> {
                    log.warn("Failed to create card info - user not found with id: {}", request.userId());
                    return new UserNotFoundException("User not found with id: " + request.userId());
                });

        CardInfo cardInfo = cardInfoMapper.toEntity(request, user);
        cardInfo = cardInfoRepository.save(cardInfo);
        log.info("Successfully created card info with id: {} for user: {}", cardInfo.getId(), request.userId());

        cacheService.evict(CacheKeys.getUserWithCardsKey(user.getId()));
        log.debug("Evicted user {} from cache", user.getId());

        return cardInfoMapper.toResponse(cardInfo);
    }

    @Override
    @Transactional
    public void deleteCardInfo(UUID id) {
        log.debug("Attempting to delete card info with id: {}", id);
        
        CardInfo cardInfo = cardInfoRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Failed to delete card info - not found with id: {}", id);
                    return new CardNotFoundException("Card not found with id: " + id);
                });
        
        UUID userId = cardInfo.getUser().getId();
        
        cardInfoRepository.deleteById(id);
        log.info("Successfully deleted card info with id: {}", id);
        
        cacheService.evict(CacheKeys.getUserWithCardsKey(userId));
        log.debug("Evicted user {} from cache due to card deletion", userId);
    }

    @Override
    public CardInfoResponse getCardInfoById(UUID id) {
        log.debug("Retrieving card info by id: {}", id);
        
        return cardInfoRepository.findByIdJPQL(id)
                .map(cardInfoMapper::toResponse)
                .orElseThrow(() -> {
                    log.warn("Card info not found with id: {}", id);
                    return new CardNotFoundException("Card not found with id: " + id);
                });
    }

    @Override
    public List<CardInfoResponse> getCardInfoByUserId(UUID userId) {
        log.debug("Retrieving all cards for user with id: {}", userId);

        List<CardInfo> cards = cardInfoRepository.findByUserId(userId);
        if (cards.isEmpty() && !userRepository.existsById(userId)) {
            log.warn("Failed to retrieve cards - user not found with id: {}", userId);
            throw new UserNotFoundException("User not found with id: " + userId);
        }

        List<CardInfoResponse> cardResponses = cards.stream()
                .map(cardInfoMapper::toResponse)
                .toList();
        log.info("Found {} cards for user with id: {}", cardResponses.size(), userId);
        return cardResponses;
    }
} 