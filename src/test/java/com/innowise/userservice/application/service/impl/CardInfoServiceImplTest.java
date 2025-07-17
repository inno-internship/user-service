package com.innowise.userservice.application.service.impl;

import com.innowise.userservice.application.dto.request.CreateCardInfoRequest;
import com.innowise.userservice.application.dto.response.CardInfoResponse;
import com.innowise.userservice.application.exception.notfound.CardNotFoundException;
import com.innowise.userservice.application.exception.notfound.UserNotFoundException;
import com.innowise.userservice.application.mapper.CardInfoMapper;
import com.innowise.userservice.application.service.CacheService;
import com.innowise.userservice.domain.entity.CardInfo;
import com.innowise.userservice.domain.entity.User;
import com.innowise.userservice.domain.repository.CardInfoRepository;
import com.innowise.userservice.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardInfoServiceImplTest {

    @Mock
    private CardInfoRepository cardInfoRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CardInfoMapper cardInfoMapper;

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private CardInfoServiceImpl cardInfoService;

    private CreateCardInfoRequest createCardInfoRequest;
    private User user;
    private CardInfo cardInfo;
    private CardInfoResponse cardInfoResponse;
    
    private final UUID userId = UUID.randomUUID();
    private final UUID cardId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        createCardInfoRequest = new CreateCardInfoRequest(userId, "1234567890123456", "John Doe", LocalDate.of(2025, 12, 31));
        user = new User();
        user.setId(userId);
        cardInfo = new CardInfo();
        cardInfo.setId(cardId);
        cardInfo.setUser(user);
        cardInfoResponse = new CardInfoResponse(cardId, "1234567890123456", "John Doe", LocalDate.of(2025, 12, 31));
    }

    @Test
    @DisplayName("Test create card info successfully")
    void createCardInfo_whenUserExists_shouldCreateCardInfo() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(user));
        when(cardInfoMapper.toEntity(any(CreateCardInfoRequest.class), any(User.class))).thenReturn(cardInfo);
        when(cardInfoRepository.save(any(CardInfo.class))).thenReturn(cardInfo);
        doNothing().when(cacheService).evict(anyString());
        when(cardInfoMapper.toResponse(any(CardInfo.class))).thenReturn(cardInfoResponse);

        CardInfoResponse result = cardInfoService.createCardInfo(createCardInfoRequest);

        assertNotNull(result);
        assertEquals(cardInfoResponse, result);

        verify(userRepository).findById(userId);
        verify(cardInfoMapper).toEntity(createCardInfoRequest, user);
        verify(cardInfoRepository).save(cardInfo);
        verify(cacheService).evict(anyString());
        verify(cardInfoMapper).toResponse(cardInfo);
    }

    @Test
    @DisplayName("Test create card info throws UserNotFoundException")
    void createCardInfo_whenUserNotFound_shouldThrowUserNotFoundException() {
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            cardInfoService.createCardInfo(createCardInfoRequest);
        });

        verify(userRepository).findById(userId);
        verify(cardInfoRepository, never()).save(any(CardInfo.class));
    }

    @Test
    @DisplayName("Test delete card info successfully")
    void deleteCardInfo_whenCardExists_shouldDeleteCard() {
        when(cardInfoRepository.findById(any(UUID.class))).thenReturn(Optional.of(cardInfo));
        doNothing().when(cardInfoRepository).deleteById(any(UUID.class));
        doNothing().when(cacheService).evict(anyString());

        cardInfoService.deleteCardInfo(cardId);

        verify(cardInfoRepository).findById(cardId);
        verify(cardInfoRepository).deleteById(cardId);
        verify(cacheService).evict(anyString());
    }

    @Test
    @DisplayName("Test delete card info throws CardNotFoundException")
    void deleteCardInfo_whenCardNotFound_shouldThrowCardNotFoundException() {
        when(cardInfoRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> {
            cardInfoService.deleteCardInfo(cardId);
        });

        verify(cardInfoRepository).findById(cardId);
        verify(cardInfoRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Test get card info by id successfully")
    void getCardInfoById_whenCardExists_shouldReturnCardInfo() {
        when(cardInfoRepository.findByIdJPQL(any(UUID.class))).thenReturn(Optional.of(cardInfo));
        when(cardInfoMapper.toResponse(any(CardInfo.class))).thenReturn(cardInfoResponse);

        CardInfoResponse result = cardInfoService.getCardInfoById(cardId);

        assertNotNull(result);
        assertEquals(cardInfoResponse, result);

        verify(cardInfoRepository).findByIdJPQL(cardId);
        verify(cardInfoMapper).toResponse(cardInfo);
    }

    @Test
    @DisplayName("Test get card info by id throws CardNotFoundException")
    void getCardInfoById_whenCardNotFound_shouldThrowCardNotFoundException() {
        when(cardInfoRepository.findByIdJPQL(any(UUID.class))).thenReturn(Optional.empty());

        assertThrows(CardNotFoundException.class, () -> {
            cardInfoService.getCardInfoById(cardId);
        });

        verify(cardInfoRepository).findByIdJPQL(cardId);
        verify(cardInfoMapper, never()).toResponse(any(CardInfo.class));
    }

    @Test
    @DisplayName("Test get card info by user id successfully")
    void getCardInfoByUserId_whenUserExists_shouldReturnCardInfoList() {
        when(cardInfoRepository.findByUserId(any(UUID.class))).thenReturn(Collections.singletonList(cardInfo));
        when(cardInfoMapper.toResponse(any(CardInfo.class))).thenReturn(cardInfoResponse);

        List<CardInfoResponse> result = cardInfoService.getCardInfoByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(cardInfoResponse, result.get(0));

        verify(cardInfoRepository).findByUserId(userId);
        verify(cardInfoMapper).toResponse(cardInfo);
    }

    @Test
    @DisplayName("Test get card info by user id with no cards")
    void getCardInfoByUserId_whenUserHasNoCards_shouldReturnEmptyList() {
        when(cardInfoRepository.findByUserId(any(UUID.class))).thenReturn(Collections.emptyList());
        when(userRepository.existsById(any(UUID.class))).thenReturn(true);

        List<CardInfoResponse> result = cardInfoService.getCardInfoByUserId(userId);

        assertNotNull(result);
        assertEquals(0, result.size());

        verify(cardInfoRepository).findByUserId(userId);
        verify(userRepository).existsById(userId);
    }

    @Test
    @DisplayName("Test get card info by user id throws UserNotFoundException")
    void getCardInfoByUserId_whenUserNotFound_shouldThrowUserNotFoundException() {
        when(cardInfoRepository.findByUserId(any(UUID.class))).thenReturn(Collections.emptyList());
        when(userRepository.existsById(any(UUID.class))).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> {
            cardInfoService.getCardInfoByUserId(userId);
        });

        verify(cardInfoRepository).findByUserId(userId);
        verify(userRepository).existsById(userId);
    }
} 