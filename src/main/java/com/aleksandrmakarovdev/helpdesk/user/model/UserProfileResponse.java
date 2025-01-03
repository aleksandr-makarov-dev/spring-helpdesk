package com.aleksandrmakarovdev.helpdesk.user.model;

import java.util.UUID;
import java.util.List;

public record UserProfileResponse(
        UUID id, String email, List<String> roles
) {
}
