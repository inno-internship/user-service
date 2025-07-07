package com.innowise.userservice.application.service.impl;

import com.innowise.userservice.application.dto.request.CreateCardInfoRequest;
import com.innowise.userservice.application.dto.response.CardInfoResponse;
import com.innowise.userservice.application.exception.CardNotFoundException;
import com.innowise.userservice.application.exception.UserNotFoundException;
import com.innowise.userservice.application.mapper.CardInfoMapper;
import com.innowise.userservice.application.service.CardInfoService;
import com.innowise.userservice.domain.entity.CardInfo;
import com.innowise.userservice.domain.entity.User;
import com.innowise.userservice.domain.repository.CardInfoRepository;
import com.innowise.userservice.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CardInfoServiceImpl implements CardInfoService {

    private final CardInfoRepository cardInfoRepository;
    private final UserRepository userRepository;
    private final CardInfoMapper cardInfoMapper;

    @Override
    @Transactional
    public CardInfoResponse createCardInfo(CreateCardInfoRequest request) {
        User user = userRepository.findById(request.userId())
                .orElseThrow(() -> new UserNotFoundException(request.userId()));

        CardInfo cardInfo = cardInfoMapper.toEntity(request, user);
        cardInfo = cardInfoRepository.save(cardInfo);
        return cardInfoMapper.toResponse(cardInfo);
    }

    @Override
    @Transactional
    public void deleteCardInfo(UUID id) {
        if (!cardInfoRepository.existsById(id)) {
            throw new CardNotFoundException(id);
        }
        cardInfoRepository.deleteById(id);
    }

    @Override
    public CardInfoResponse getCardInfoById(UUID id) {
        return cardInfoRepository.findByIdJPQL(id)
                .map(cardInfoMapper::toResponse)
                .orElseThrow(() -> new CardNotFoundException(id));
    }

    @Override
    public List<CardInfoResponse> getCardInfoByUserId(UUID userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
        return cardInfoRepository.findByUserId(userId).stream()
                .map(cardInfoMapper::toResponse)
                .toList();
    }
} 