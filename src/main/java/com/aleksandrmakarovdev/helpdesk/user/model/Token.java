package com.aleksandrmakarovdev.helpdesk.user.model;

import java.util.Date;

public record Token(String token, Date issuedAt, Date expiresAt) {
}
