package com.aleksandrmakarovdev.helpdesk.user.service;

import com.aleksandrmakarovdev.helpdesk.exception.RoleNotFoundException;
import com.aleksandrmakarovdev.helpdesk.exception.UserFoundException;
import com.aleksandrmakarovdev.helpdesk.user.model.RoleName;
import com.aleksandrmakarovdev.helpdesk.user.repository.RoleRepository;
import com.aleksandrmakarovdev.helpdesk.user.repository.UserRepository;
import com.aleksandrmakarovdev.helpdesk.user.entity.Role;
import com.aleksandrmakarovdev.helpdesk.user.entity.User;
import com.aleksandrmakarovdev.helpdesk.user.model.CreateUserRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {

    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

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
                .build();

        // Save user to the database
        userRepository.save(userToCreate);
    }
}
