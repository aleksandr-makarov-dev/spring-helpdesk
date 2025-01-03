package com.aleksandrmakarovdev.helpdesk.user;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
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
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().json("""
                        {
                        "message": "User 'testuser@example.com' has been registered successfully."
                        }
                        """));
    }

    @Test
    @DisplayName("Try to register email that already registered should return conflict")
    void registerUser_whenUserAlreadyExists_shouldReturnConflict() {

    }

    @Test
    @DisplayName("Try to register user with invalid credentials should return validation error")
    void registerUser_whenInvalidCredentials_shouldReturnValidationError() {

    }

    @AfterEach
    void tearDown() {

    }
}