package com.innowise.userservice.integration.controller;

import com.innowise.userservice.integration.AbstractIntegrationTest;
import com.innowise.userservice.application.dto.request.CreateCardInfoRequest;
import com.innowise.userservice.application.dto.request.CreateUserRequest;
import com.innowise.userservice.application.dto.response.CardInfoResponse;
import com.innowise.userservice.application.dto.response.UserResponse;
import com.innowise.userservice.domain.repository.CardInfoRepository;
import com.innowise.userservice.domain.repository.UserRepository;
import com.innowise.userservice.infrastructure.cache.CacheKeys;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class CardInfoControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String CARD_API_URL = "/api/cards";
    private static final String USER_API_URL = "/api/users";

    private static final String DEFAULT_CARD_NUMBER = "1111222233334444";
    private static final String DEFAULT_HOLDER_NAME = "John A Doe";
    private static final LocalDate DEFAULT_EXPIRATION_DATE = LocalDate.now().plusYears(2);

    @Autowired
    private CardInfoRepository cardInfoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void tearDown() {
        cleanupData();
    }

    @Nested
    @DisplayName("Endpoint: POST /cards")
    class CreateCardEndpoint {

        @Test
        @DisplayName("Success and Cache Eviction")
        void whenCreateCard_thenReturns201AndEvictsUserCache() throws Exception {
            UserResponse user = createDefaultUser();
            String userCacheKey = CacheKeys.getUserWithCardsKey(user.id());

            mockMvc.perform(get(USER_API_URL + "/{id}", user.id())).andExpect(status().isOk());
            assertThat(redisTemplate.hasKey(userCacheKey)).isTrue();

            CreateCardInfoRequest request = createDefaultCardRequest(user.id());

            mockMvc.perform(post(CARD_API_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.number").value(DEFAULT_CARD_NUMBER))
                    .andExpect(jsonPath("$.holder").value(DEFAULT_HOLDER_NAME));

            assertThat(cardInfoRepository.count()).isEqualTo(1);
            assertThat(redisTemplate.hasKey(userCacheKey)).isFalse();
        }

        @Test
        @DisplayName("User Not Found returns 404")
        void whenCreateCard_forNonExistingUser_thenReturns404() throws Exception {
            UUID nonExistingUserId = UUID.randomUUID();
            CreateCardInfoRequest request = createDefaultCardRequest(nonExistingUserId);

            assertPostApiError(CARD_API_URL, "User not found with id: " + nonExistingUserId, 404, request);
        }

        @Test
        @DisplayName("Invalid Request returns 400")
        void whenCreateCard_withInvalidData_thenReturns400() throws Exception {
            UserResponse user = createDefaultUser();
            CreateCardInfoRequest request = new CreateCardInfoRequest(
                    user.id(), "123", "", null);

            mockMvc.perform(post(CARD_API_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("Endpoint: GET /cards")
    class GetCardsEndpoint {

        @Test
        @DisplayName("Get By Id - Success")
        void whenGetCardById_ifExists_thenReturns200() throws Exception {
            CardInfoResponse card = createDefaultCard();

            mockMvc.perform(get(CARD_API_URL + "/{id}", card.id()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(card.id().toString()))
                    .andExpect(jsonPath("$.number").value(DEFAULT_CARD_NUMBER))
                    .andExpect(jsonPath("$.holder").value(DEFAULT_HOLDER_NAME));
        }

        @Test
        @DisplayName("Get By Id - Not Found returns 404")
        void whenGetCardById_ifNotExists_thenReturns404() throws Exception {
            UUID nonExistingId = UUID.randomUUID();
            assertGetApiError(CARD_API_URL + "/" + nonExistingId, "Card not found with id: " + nonExistingId, 404);
        }

        @Test
        @DisplayName("Get By User Id - Success")
        void whenGetCardsByUserId_ifUserAndCardsExist_thenReturns200() throws Exception {
            UserResponse user = createDefaultUser();
            CardInfoResponse firstCard = createCardViaApi(createDefaultCardRequest(user.id()));
            CardInfoResponse secondCard = createCardViaApi(new CreateCardInfoRequest(user.id(), "5555666677778888", "Jane B Doe", LocalDate.now().plusYears(3)));

            mockMvc.perform(get(CARD_API_URL + "/user/{userId}", user.id()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].id", containsInAnyOrder(firstCard.id().toString(), secondCard.id().toString())));
        }

        @Test
        @DisplayName("Get By User Id - Success with Empty List")
        void whenGetCardsByUserId_ifUserExistsButHasNoCards_thenReturns200AndEmptyList() throws Exception {
            UserResponse user = createDefaultUser();

            mockMvc.perform(get(CARD_API_URL + "/user/{userId}", user.id()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }

        @Test
        @DisplayName("Get By User Id - User Not Found returns 404")
        void whenGetCardsByUserId_ifUserNotExists_thenReturns404() throws Exception {
            UUID nonExistingUserId = UUID.randomUUID();
            assertGetApiError(CARD_API_URL + "/user/" + nonExistingUserId, "User not found with id: " + nonExistingUserId, 404);
        }
    }

    @Nested
    @DisplayName("Endpoint: DELETE /cards/{id}")
    class DeleteCardEndpoint {

        @Test
        @DisplayName("Success and Cache Eviction")
        void whenDeleteCard_thenReturns204AndEvictsCache() throws Exception {
            UserResponse user = createDefaultUser();
            CardInfoResponse card = createCardViaApi(createDefaultCardRequest(user.id()));
            String userCacheKey = CacheKeys.getUserWithCardsKey(user.id());

            mockMvc.perform(get(USER_API_URL + "/{id}", user.id())).andExpect(status().isOk());
            assertThat(redisTemplate.hasKey(userCacheKey)).isTrue();

            mockMvc.perform(delete(CARD_API_URL + "/{id}", card.id()))
                    .andExpect(status().isNoContent());

            assertThat(cardInfoRepository.existsById(card.id())).isFalse();
            assertThat(redisTemplate.hasKey(userCacheKey)).isFalse();
        }

        @Test
        @DisplayName("Not Found returns 404")
        void whenDeleteCard_ifNotExists_thenReturns404() throws Exception {
            UUID nonExistingId = UUID.randomUUID();
            assertDeleteApiError(CARD_API_URL + "/" + nonExistingId, "Card not found with id: " + nonExistingId, 404);
        }
    }

    private void cleanupData() {
        cardInfoRepository.deleteAll();
        userRepository.deleteAll();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    private UserResponse createDefaultUser() throws Exception {
        CreateUserRequest request = new CreateUserRequest("Default", "User", LocalDate.of(1990, 1, 1), "default-user-for-card@example.com");
        String responseBody = mockMvc.perform(post(USER_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(responseBody, UserResponse.class);
    }

    private CreateCardInfoRequest createDefaultCardRequest(UUID userId) {
        return new CreateCardInfoRequest(userId, DEFAULT_CARD_NUMBER, DEFAULT_HOLDER_NAME, DEFAULT_EXPIRATION_DATE);
    }

    private CardInfoResponse createDefaultCard() throws Exception {
        UserResponse user = createDefaultUser();
        return createCardViaApi(createDefaultCardRequest(user.id()));
    }

    private CardInfoResponse createCardViaApi(CreateCardInfoRequest request) throws Exception {
        String responseBody = mockMvc.perform(post(CARD_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(responseBody, CardInfoResponse.class);
    }

    private void assertGetApiError(String path, String message, int statusCode) throws Exception {
        mockMvc.perform(get(path))
                .andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.statusCode").value(statusCode));
    }

    private void assertPostApiError(String path, String message, int statusCode, Object requestBody) throws Exception {
        mockMvc.perform(post(path)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.statusCode").value(statusCode));
    }

    private void assertDeleteApiError(String path, String message, int statusCode) throws Exception {
        mockMvc.perform(delete(path))
                .andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.statusCode").value(statusCode));
    }
}