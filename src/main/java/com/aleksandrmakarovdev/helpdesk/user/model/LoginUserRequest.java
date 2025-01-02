package com.aleksandrmakarovdev.helpdesk.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginUserRequest {

    @NotBlank(message = "Email address must be not empty.")
    @Email(message = "Email address has invalid format.")
    @Size(max = 320, message = "Email address must not be longer than {max} characters.")
    private String email;

    @NotBlank(message = "Password must be not empty.")
    @Size(min = 6, max = 72, message = "Password must be between {min} and {max} characters.")
    private String password;
}