package com.aleksandrmakarovdev.helpdesk.user.service;

import com.aleksandrmakarovdev.helpdesk.exception.RoleNotFoundException;
import com.aleksandrmakarovdev.helpdesk.exception.UserFoundException;
import com.aleksandrmakarovdev.helpdesk.exception.UserNotFoundException;
import com.aleksandrmakarovdev.helpdesk.security.WebUserDetails;
import com.aleksandrmakarovdev.helpdesk.user.model.*;
import com.aleksandrmakarovdev.helpdesk.user.repository.RoleRepository;
import com.aleksandrmakarovdev.helpdesk.user.repository.UserRepository;
import com.aleksandrmakarovdev.helpdesk.user.entity.Role;
import com.aleksandrmakarovdev.helpdesk.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    /**
     * Creates a new user in the system with the provided email and password.
     * Ensures the user does not already exist, assigns the default USER_ROLE,
     * hashes the password, and saves the user to the database.
     *
     * @param createUserRequest The user's registration details (email and password).
     * @throws UserFoundException    If a user with the same email already exists.
     * @throws RoleNotFoundException If the default user role cannot be found.
     */
    @Override
    @Transactional
    public void createUser(CreateUserRequest createUserRequest) {

        Optional<User> foundUser = userRepository.findByEmail(createUserRequest.getEmail());

        // Check if email address is already registered
        if (foundUser.isPresent()) {
            throw new UserFoundException(String.format("User '%s' already exists.", createUserRequest.getEmail()));
        }

        // Get USER_ROLE entity by name
        Optional<Role> userRole = roleRepository.findByName(RoleName.ROLE_USER.name());

        if (userRole.isEmpty()) {
            throw new RoleNotFoundException(String.format("Role '%s' not found.", RoleName.ROLE_USER.name()));
        }

        // Encode password with Bcrypt Encoder
        String passwordHash = passwordEncoder.encode(createUserRequest.getPassword());

        User userToCreate = User
                .builder()
                .email(createUserRequest.getEmail())
                .username(createUserRequest.getEmail())
                .passwordHash(passwordHash)
                .roles(List.of(userRole.get()))
                .createdAt(Date.from(Instant.now()))
                .build();

        // Save user to the database
        userRepository.save(userToCreate);
    }

    /**
     * Authenticates a user with the provided email and password.
     * Issues a pair of tokens (refresh and access) upon successful authentication.
     *
     * @param loginUserRequest The user's login credentials (email and password).
     * @return A {@link TokensResponse} object containing the refresh and access tokens.
     * @throws AuthenticationException If authentication fails.
     */
    @Override
    @Transactional
    public TokensResponse loginUser(LoginUserRequest loginUserRequest) {

        // Try to authenticate user with email and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserRequest.getEmail(), loginUserRequest.getPassword())
        );

        // Extract authenticated user's details
        WebUserDetails userDetails = (WebUserDetails) authentication.getPrincipal();

        // Generate tokens for the authenticated user
        Token refreshToken = tokenService.createRefreshToken(userDetails);
        Token accessToken = tokenService.createAccessToken(userDetails);

        return new TokensResponse(refreshToken, accessToken);
    }

    @Override
    public UserProfileResponse getUserProfile(UUID userId) {
        return userRepository.findById(userId)
                .map(user -> new UserProfileResponse(user.getId(), user.getEmail(), user.getRoles().stream().map(Role::getName).toList()))
                .orElseThrow(() -> new UserNotFoundException(String.format("User '%s' not found", userId.toString())));
    }
}
