package com.aleksandrmakarovdev.helpdesk.user;

import com.aleksandrmakarovdev.helpdesk.base.MessageResponse;
import com.aleksandrmakarovdev.helpdesk.user.model.CreateUserRequest;
import com.aleksandrmakarovdev.helpdesk.user.model.LoginUserRequest;
import com.aleksandrmakarovdev.helpdesk.user.model.TokensResponse;
import com.aleksandrmakarovdev.helpdesk.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UserService userService;

    @PostMapping("register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody CreateUserRequest createUserRequest) {

        userService.createUser(createUserRequest);

        MessageResponse messageResponse = new MessageResponse(
                String.format("User '%s' has been registered successfully.", createUserRequest.getEmail())
        );

        return ResponseEntity.ok().body(messageResponse);
    }

    @PostMapping("login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginUserRequest loginUserRequest) {

        TokensResponse tokensResponse = userService.loginUser(loginUserRequest);

        return ResponseEntity.ok().body(tokensResponse);
    }
}
