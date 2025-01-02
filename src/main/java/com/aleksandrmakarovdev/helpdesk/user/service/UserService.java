package com.aleksandrmakarovdev.helpdesk.user.service;

import com.aleksandrmakarovdev.helpdesk.exception.RoleNotFoundException;
import com.aleksandrmakarovdev.helpdesk.exception.UserFoundException;
import com.aleksandrmakarovdev.helpdesk.user.model.CreateUserRequest;
import com.aleksandrmakarovdev.helpdesk.user.model.LoginUserRequest;
import org.springframework.security.core.AuthenticationException;

public interface UserService {

    /**
     * Creates a new user with the given email address and password.
     * This method checks if the email is already registered, fetches the
     * default user role, encodes the password, and saves the user to the database.
     *
     * @param createUserRequest The credentials used to create the new user.
     * @throws UserFoundException       If a user with the same email already exists.
     * @throws RoleNotFoundException    If the default user role cannot be found.
     */
    void createUser(CreateUserRequest createUserRequest);

    /**
     * Authenticates a user with the provided email and password.
     * If authentication is successful, a JWT token is issued for the user.
     *
     * @param loginUserRequest The login credentials (email and password).
     * @return A JWT token for the authenticated user.
     * @throws AuthenticationException If authentication fails.
     */
    String loginUser(LoginUserRequest loginUserRequest);
}
