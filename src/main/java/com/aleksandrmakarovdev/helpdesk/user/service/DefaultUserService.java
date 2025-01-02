package com.aleksandrmakarovdev.helpdesk.user.service;

import com.aleksandrmakarovdev.helpdesk.exception.RoleNotFoundException;
import com.aleksandrmakarovdev.helpdesk.exception.UserFoundException;
import com.aleksandrmakarovdev.helpdesk.security.WebUserDetails;
import com.aleksandrmakarovdev.helpdesk.user.model.LoginUserRequest;
import com.aleksandrmakarovdev.helpdesk.user.model.RoleName;
import com.aleksandrmakarovdev.helpdesk.user.repository.RoleRepository;
import com.aleksandrmakarovdev.helpdesk.user.repository.UserRepository;
import com.aleksandrmakarovdev.helpdesk.user.entity.Role;
import com.aleksandrmakarovdev.helpdesk.user.entity.User;
import com.aleksandrmakarovdev.helpdesk.user.model.CreateUserRequest;
import com.aleksandrmakarovdev.helpdesk.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;


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
                .createdAt(LocalDateTime.now())
                .build();

        // Save user to the database
        userRepository.save(userToCreate);
    }


    @Override
    @Transactional
    public String loginUser(LoginUserRequest loginUserRequest) {

        // Try to authenticate user with email and password
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserRequest.getEmail(), loginUserRequest.getPassword())
        );

        WebUserDetails userDetails = (WebUserDetails) authentication.getPrincipal();
        
        // TODO: create refresh token for user and save it to database

        // Issue access token for the user
        return jwtUtil.issueToken(userDetails);
    }
}
