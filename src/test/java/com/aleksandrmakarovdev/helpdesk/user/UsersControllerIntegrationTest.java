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

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
    }

    /**
     * Test: Register a user with valid credentials.
     * This test ensures that when valid credentials are provided,
     * the user is registered successfully, and the response contains a success message.
     */
    @Test
    @DisplayName("Try to register user with valid credentials should return success message")
    void registerUser_whenValidCredentials_shouldReturnSuccessMessage() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""
                        {
                        "email": "testuser@example.com",
                        "password": "testpassword"
                        }
                        """);

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                        "message": "User 'testuser@example.com' has been registered successfully."
                        }
                        """));
    }

    /**
     * Test: Attempt to register a user with an already registered email.
     * This test ensures that a conflict response is returned when the email already exists.
     */
    @Test
    @DisplayName("Try to register email that already registered should return conflict")
    void registerUser_whenUserAlreadyExists_shouldReturnConflict() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""
                        {
                        "email": "testuser@example.com",
                        "password": "testpassword"
                        }
                        """);

        mockMvc.perform(requestBuilder); // First registration to set up the scenario

        mockMvc.perform(requestBuilder)
                .andDo(print())
                .andExpect(status().isConflict())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(content().json("""
                        {
                        "detail": "User 'testuser@example.com' already exists."
                        }
                        """));
    }

    /**
     * Test: Attempt to register a user with invalid credentials.
     * This test ensures that validation errors are handled correctly and an appropriate response is returned.
     */
    @Test
    @DisplayName("Try to register user with invalid credentials should return validation error")
    void registerUser_whenInvalidCredentials_shouldReturnValidationError() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content("""
                        {
                        "email": "invalidemail",
                        "password": "12345678"
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
        // Clean up the database after each test to ensure test isolation
        userRepository.deleteAll();
    }
}
