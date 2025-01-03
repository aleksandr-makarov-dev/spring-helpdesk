package com.aleksandrmakarovdev.helpdesk.user;

import com.aleksandrmakarovdev.helpdesk.base.MessageResponse;
import com.aleksandrmakarovdev.helpdesk.user.model.CreateUserRequest;
import com.aleksandrmakarovdev.helpdesk.user.model.LoginUserRequest;
import com.aleksandrmakarovdev.helpdesk.user.model.TokensResponse;
import com.aleksandrmakarovdev.helpdesk.user.model.UserProfileResponse;
import com.aleksandrmakarovdev.helpdesk.user.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Tag(name = "Users", description = "Endpoints for user registration and authentication")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    /**
     * Endpoint to register a new user.
     * This method accepts user registration details, processes the registration,
     * and returns a success message upon successful registration.
     *
     * @param createUserRequest The user's registration details (email and password).
     * @return A ResponseEntity containing a success message.
     */
    @PostMapping("register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody CreateUserRequest createUserRequest) {

        // Delegate user creation to the service layer
        userService.createUser(createUserRequest);

        // Prepare a success response
        MessageResponse messageResponse = new MessageResponse(
                String.format("User '%s' has been registered successfully.", createUserRequest.getEmail())
        );

        return ResponseEntity.ok().body(messageResponse);
    }

    /**
     * Endpoint to authenticate a user and log them in.
     * This method accepts login credentials, validates them, and issues access and refresh tokens.
     * The refresh token is stored in an HTTP-only cookie for enhanced security.
     *
     * @param loginUserRequest The user's login credentials (email and password).
     * @param response         The HTTP response to add the refresh token cookie.
     * @return A ResponseEntity containing the issued tokens.
     */
    @PostMapping("login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginUserRequest loginUserRequest, HttpServletResponse response) {

        // Authenticate the user and generate tokens
        TokensResponse tokensResponse = userService.loginUser(loginUserRequest);

        // Calculate the expiration time for the refresh token in seconds
        int maxAge = (int) ChronoUnit.SECONDS.between(Instant.now(), tokensResponse.refreshToken().expiresAt().toInstant());

        // Create a secure, HTTP-only cookie to store the refresh token
        Cookie cookie = new Cookie("refresh-token", tokensResponse.refreshToken().token());
        cookie.setPath("/");              // Set cookie path for the entire application
        cookie.setDomain(null);           // No specific domain; defaults to the request domain
        cookie.setSecure(true);           // Use secure cookie (requires HTTPS)
        cookie.setHttpOnly(true);         // Make cookie inaccessible via JavaScript
        cookie.setMaxAge(maxAge);         // Set cookie's max age

        // Add the cookie to the HTTP response
        response.addCookie(cookie);

        // Return access token in response body and refresh token in cookie
        return ResponseEntity.ok().body(tokensResponse.accessToken());
    }

    @GetMapping("profile")
    public ResponseEntity<?> getUserProfile() {
        UserProfileResponse userProfileResponse = userService.getUserProfile(UUID.randomUUID());

        return ResponseEntity.ok().body(userProfileResponse);
    }
}
