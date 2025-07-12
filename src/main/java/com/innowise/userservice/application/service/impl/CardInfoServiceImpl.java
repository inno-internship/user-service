package com.innowise.userservice.application.service.impl;

import com.innowise.userservice.application.dto.request.CreateCardInfoRequest;
import com.innowise.userservice.application.dto.response.CardInfoResponse;
import com.innowise.userservice.application.exception.notfound.CardNotFoundException;
import com.innowise.userservice.application.exception.notfound.UserNotFoundException;
import com.innowise.userservice.application.mapper.CardInfoMapper;
import com.innowise.userservice.application.service.CardInfoService;
import com.innowise.userservice.domain.entity.CardInfo;
import com.innowise.userservice.domain.entity.User;
import com.innowise.userservice.domain.repository.CardInfoRepository;
import com.innowise.userservice.domain.repository.UserRepository;
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
        return cardInfoMapper.toResponse(cardInfo);
    }

    @Override
    @Transactional
    public void deleteCardInfo(UUID id) {
        log.debug("Attempting to delete card info with id: {}", id);
        
        if (!cardInfoRepository.existsById(id)) {
            log.warn("Failed to delete card info - not found with id: {}", id);
            throw new CardNotFoundException("Card not found with id: " + id);
        }
        
        cardInfoRepository.deleteById(id);
        log.info("Successfully deleted card info with id: {}", id);
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
        
        if (!userRepository.existsById(userId)) {
            log.warn("Failed to retrieve cards - user not found with id: {}", userId);
            throw new UserNotFoundException("User not found with id: " + userId);
        }
        
        List<CardInfoResponse> cards = cardInfoRepository.findByUserId(userId).stream()
                .map(cardInfoMapper::toResponse)
                .toList();
        log.info("Found {} cards for user with id: {}", cards.size(), userId);
        return cards;
    }
} 