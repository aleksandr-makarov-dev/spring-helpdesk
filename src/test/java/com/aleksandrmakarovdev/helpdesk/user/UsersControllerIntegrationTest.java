package com.aleksandrmakarovdev.helpdesk.user;

import com.aleksandrmakarovdev.helpdesk.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class UsersControllerIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>(DockerImageName.parse("postgres:17.2"));

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach()
    void setUp() {
    }

    @Test
    @DisplayName("Try to register user with valid credentials should return success message")
    void registerUser_whenValidCredentials_shouldReturnSuccessMessage() throws Exception {

        // Given

        var requestBuilder = MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""
                        {
                        "email": "testuser@example.com",
                        "password": "testpassword"
                        }
                        """);

        // When

        mockMvc.perform(requestBuilder)
                // Then
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                        "message": "User 'testuser@example.com' has been registered successfully."
                        }
                        """));
    }

    @Test
    @DisplayName("Try to register email that already registered should return conflict")
    void registerUser_whenUserAlreadyExists_shouldReturnConflict() throws Exception {

        // Given
        var requestBuilder = MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""
                        {
                        "email": "testuser@example.com",
                        "password": "testpassword"
                        }
                        """);

        mockMvc.perform(requestBuilder);

        // When

        mockMvc.perform(requestBuilder)
                // Then
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(content().json("""
                        {
                        "detail": "User 'testuser@example.com' already exists."
                        }
                        """));
    }

    @Test
    @DisplayName("Try to register user with invalid credentials should return validation error")
    void registerUser_whenInvalidCredentials_shouldReturnValidationError() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""
                        {
                        "email": "invalidemail",
                        "password": "123"
                        }
                        """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(content().json("""
                        {"detail": "Email address has invalid format."}
                        """));
    }


    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }
}