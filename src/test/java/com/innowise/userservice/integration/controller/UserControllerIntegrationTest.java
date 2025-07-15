package com.innowise.userservice.integration.controller;

import com.innowise.userservice.integration.AbstractIntegrationTest;
import com.innowise.userservice.application.dto.request.CreateUserRequest;
import com.innowise.userservice.application.dto.request.UpdateUserRequest;
import com.innowise.userservice.application.dto.response.UserResponse;
import com.innowise.userservice.domain.repository.UserRepository;
import com.innowise.userservice.infrastructure.cache.CacheKeys;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.containsInAnyOrder;

class UserControllerIntegrationTest extends AbstractIntegrationTest {

    private static final String USER_API_URL = "/api/users";
    private static final String DEFAULT_FIRST_NAME = "John";
    private static final String DEFAULT_LAST_NAME = "Doe";
    private static final String DEFAULT_EMAIL = "john.doe@example.com";
    private static final LocalDate DEFAULT_BIRTH_DATE = LocalDate.of(1983, 8, 21);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @AfterEach
    void tearDown() {
        cleanupData();
    }

    @Nested
    @DisplayName("POST /users endpoints")
    class CreateUserEndpoint {
        @Test
        @DisplayName("Success")
        void whenCreateUser_thenReturns201AndUserResponse() throws Exception {
            CreateUserRequest request = createDefaultUserRequest();

            mockMvc.perform(post(USER_API_URL)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.id").isNotEmpty())
                    .andExpect(jsonPath("$.name").value(DEFAULT_FIRST_NAME))
                    .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));

            assertThat(userRepository.count()).isEqualTo(1);
            assertThat(userRepository.findByEmail(request.email())).isPresent();
        }

        @Test
        @DisplayName("Email Already Exists")
        void whenCreateUser_withExistingEmail_thenReturns409() throws Exception {
            createDefaultUser();
            CreateUserRequest request = createDefaultUserRequest();
            
            mockMvc.perform(post(USER_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message").value("User with email " + DEFAULT_EMAIL + " already exists"));
        }

        @Test
        @DisplayName("Invalid Request")
        void whenCreateUser_withInvalidRequest_thenReturns400() throws Exception {
            CreateUserRequest request = new CreateUserRequest(
                    "", "", null, "invalid-email");

            mockMvc.perform(post(USER_API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("GET /users endpoints")
    class GetUsersEndpoint {
        @Test
        @DisplayName("Get All - Success")
        void whenGetAllUsers_thenReturnsListOfUsers() throws Exception {
            UserResponse firstUser = createDefaultUser();
            
            CreateUserRequest secondRequest = new CreateUserRequest(
                    "Jane", "Smith", DEFAULT_BIRTH_DATE,
                    "jane.smith@example.com");
            UserResponse secondUser = createUserViaApi(secondRequest);

            mockMvc.perform(get(USER_API_URL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].id").exists())
                    .andExpect(jsonPath("$[1].id").exists())
                    .andExpect(jsonPath("$[*].email", containsInAnyOrder(
                            firstUser.email(), secondUser.email()
                    )));
        }

        @Test
        @DisplayName("Get By Id - Success")
        void shouldReturnUserById_whenUserExists() throws Exception {
            UserResponse createdUser = createDefaultUser();
            UUID userId = createdUser.id();

            mockMvc.perform(get(USER_API_URL + "/{id}", userId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(userId.toString()))
                    .andExpect(jsonPath("$.name").value(DEFAULT_FIRST_NAME))
                    .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
        }

        @Test
        @DisplayName("Get By Id - Not Found")
        void whenGetUserById_withNonExistingId_thenReturns404() throws Exception {
            UUID nonExistingId = UUID.randomUUID();
            String path = USER_API_URL + "/" + nonExistingId;
            assertGetApiError(path, "User not found with id: " + nonExistingId, 404);
        }

        @Test
        @DisplayName("Get By Email - Success")
        void whenGetUserByEmail_thenReturnsUser() throws Exception {
            UserResponse createdUser = createDefaultUser();

            mockMvc.perform(get(USER_API_URL + "/email/" + DEFAULT_EMAIL))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(createdUser.id().toString()))
                    .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
                    .andExpect(jsonPath("$.name").value(DEFAULT_FIRST_NAME));
        }

        @Test
        @DisplayName("Get By Email - Not Found")
        void whenGetUserByEmail_withNonExistingEmail_thenReturns404() throws Exception {
            String nonExistingEmail = "nonexisting@example.com";
            String path = USER_API_URL + "/email/" + nonExistingEmail;
            assertGetApiError(path, "User not found with email: " + nonExistingEmail, 404);
        }

        @Test
        @DisplayName("Get By Ids - Success")
        void whenGetUsersByIds_thenReturnsUsers() throws Exception {
            UserResponse firstUser = createDefaultUser();
            
            CreateUserRequest secondRequest = new CreateUserRequest(
                    "Jane", "Smith", DEFAULT_BIRTH_DATE,
                    "jane.smith@example.com");
            UserResponse secondUser = createUserViaApi(secondRequest);

            String url = String.format("%s/by-ids?ids=%s,%s", 
                    USER_API_URL, firstUser.id(), secondUser.id());

            mockMvc.perform(get(url))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[*].id", containsInAnyOrder(
                            firstUser.id().toString(), secondUser.id().toString()
                    )))
                    .andExpect(jsonPath("$[*].email", containsInAnyOrder(
                            firstUser.email(), secondUser.email()
                    )));
        }

        @Test
        @DisplayName("Get By Ids - Not Found")
        void whenGetUsersByIds_withNonExistingId_thenReturns404() throws Exception {
            UserResponse existingUser = createDefaultUser();
            UUID nonExistingId = UUID.randomUUID();

            String url = String.format("%s/by-ids?ids=%s,%s", 
                    USER_API_URL, existingUser.id(), nonExistingId);

            assertGetApiError(url, "Users not found with ids: [" + nonExistingId + "]", 404);
        }

        @Test
        @DisplayName("Get By Ids - Empty List")
        void whenGetUsersByIds_withEmptyList_thenReturnsEmptyArray() throws Exception {
            mockMvc.perform(get(USER_API_URL + "/by-ids")
                    .param("ids", ""))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(0)));
        }
    }

    @Nested
    @DisplayName("PUT /users endpoints")
    class UpdateUserEndpoint {
        @Test
        @DisplayName("Success")
        void whenUpdateUser_thenReturns200AndEvictsCache() throws Exception {
            UserResponse createdUser = createDefaultUser();
            UUID userId = createdUser.id();
            String cacheKey = CacheKeys.getUserWithCardsKey(userId);

            mockMvc.perform(get(USER_API_URL + "/{id}", userId)).andExpect(status().isOk());
            assertThat(redisTemplate.hasKey(cacheKey)).isTrue();

            UpdateUserRequest updateRequest = createUpdateUserRequest();

            mockMvc.perform(put(USER_API_URL + "/{id}", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateRequest)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value("NewJohn"))
                    .andExpect(jsonPath("$.email").value("newjohn.doe@example.com"));

            assertThat(userRepository.findByEmail("newjohn.doe@example.com")).isPresent();
            assertThat(redisTemplate.hasKey(cacheKey)).isFalse();
        }

        @Test
        @DisplayName("Email Already Exists")
        void whenUpdateUser_withExistingEmail_thenReturns409() throws Exception {
            createDefaultUser();
            CreateUserRequest secondRequest = new CreateUserRequest(
                    "Jane", "Smith", DEFAULT_BIRTH_DATE,
                    "jane.smith@example.com");
            UserResponse secondUser = createUserViaApi(secondRequest);

            UpdateUserRequest updateRequest = new UpdateUserRequest(DEFAULT_FIRST_NAME, DEFAULT_LAST_NAME, DEFAULT_BIRTH_DATE, "john.doe@example.com");

            assertPutApiError(USER_API_URL + "/" + secondUser.id(),
                    "User with email " + DEFAULT_EMAIL + " already exists",
                    409, updateRequest);
        }

        @Test
        @DisplayName("Not Found")
        void whenUpdateUser_withNonExistingId_thenReturns404() throws Exception {
            UUID nonExistingId = UUID.randomUUID();
            UpdateUserRequest updateRequest = createUpdateUserRequest();

            assertPutApiError(USER_API_URL + "/" + nonExistingId,
                    "User not found with id: " + nonExistingId,
                    404, updateRequest);
        }
    }

    @Nested
    @DisplayName("DELETE /users endpoints")
    class DeleteUserEndpoint {
        @Test
        @DisplayName("Success")
        void whenDeleteUser_thenReturns204AndEvictsCache() throws Exception {
            UserResponse createdUser = createDefaultUser();
            UUID userId = createdUser.id();
            String cacheKey = CacheKeys.getUserWithCardsKey(userId);

            mockMvc.perform(get(USER_API_URL + "/{id}", userId)).andExpect(status().isOk());
            assertThat(redisTemplate.hasKey(cacheKey)).isTrue();

            mockMvc.perform(delete(USER_API_URL + "/" + userId))
                    .andExpect(status().isNoContent());

            assertThat(userRepository.existsById(userId)).isFalse();
            assertThat(redisTemplate.hasKey(cacheKey)).isFalse();
        }

        @Test
        @DisplayName("Not Found")
        void whenDeleteUser_withNonExistingId_thenReturns404() throws Exception {
            UUID nonExistingId = UUID.randomUUID();
            String path = USER_API_URL + "/" + nonExistingId;
            assertDeleteApiError(path, "User not found with id: " + nonExistingId, 404);
        }
    }

    private void cleanupData() {
        userRepository.deleteAll();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    private void testRedisConnection() {
        redisTemplate.opsForValue().set("test-key", "test-value");
        String value = redisTemplate.opsForValue().get("test-key").toString();
        assertThat(value).isEqualTo("test-value");
        System.out.println(">>> Redis connection in minimal test is OK! <<<");
    }

    private void testPostgresConnection() {
        assertThat(userRepository.count()).isEqualTo(0);
        System.out.println(">>> Postgres connection in minimal test is OK! <<<");
    }

    private void assertGetApiError(String path, String message, int statusCode) throws Exception {
        String basePath = path.contains("?") ? path.substring(0, path.indexOf("?")) : path;
        
        mockMvc.perform(get(path))
                .andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.path").value("uri=" + basePath))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.statusCode").value(statusCode))  
                .andExpect(jsonPath("$.timestamp").exists());
    }

    private void assertPutApiError(String path, String message, int statusCode, UpdateUserRequest updateRequest) throws Exception {
        String basePath = path.contains("?") ? path.substring(0, path.indexOf("?")) : path;
        
        mockMvc.perform(put(path)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.path").value("uri=" + basePath))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.statusCode").value(statusCode))  
                .andExpect(jsonPath("$.timestamp").exists());
    }

    private void assertDeleteApiError(String path, String message, int statusCode) throws Exception {
        String basePath = path.contains("?") ? path.substring(0, path.indexOf("?")) : path;
        
        mockMvc.perform(delete(path))
                .andExpect(status().is(statusCode))
                .andExpect(jsonPath("$.path").value("uri=" + basePath))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.statusCode").value(statusCode))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    private CreateUserRequest createDefaultUserRequest() {
        return new CreateUserRequest(
                DEFAULT_FIRST_NAME,
                DEFAULT_LAST_NAME,
                DEFAULT_BIRTH_DATE,
                DEFAULT_EMAIL
        );
    }

    private UpdateUserRequest createUpdateUserRequest() {
        return new UpdateUserRequest(
                "NewJohn",
                "NewDoe",
                DEFAULT_BIRTH_DATE,
                "newjohn.doe@example.com"
        );
    }

    private UserResponse createDefaultUser() throws Exception {
        return createUserViaApi(createDefaultUserRequest());
    }

    private UserResponse createUserViaApi(CreateUserRequest request) throws Exception {
        String responseBody = mockMvc.perform(post(USER_API_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readValue(responseBody, UserResponse.class);
    }

    private void performGetUserByIdAndAssertResponse(UUID userId) throws Exception {
        mockMvc.perform(get(USER_API_URL + "/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value(DEFAULT_FIRST_NAME))
                .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL));
    }
} 