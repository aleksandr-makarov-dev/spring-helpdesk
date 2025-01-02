package com.aleksandrmakarovdev.helpdesk.user.service;

import com.aleksandrmakarovdev.helpdesk.security.WebUserDetails;
import com.aleksandrmakarovdev.helpdesk.user.entity.RefreshToken;
import com.aleksandrmakarovdev.helpdesk.user.model.Token;
import com.aleksandrmakarovdev.helpdesk.user.repository.RefreshTokenRepository;
import com.aleksandrmakarovdev.helpdesk.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DefaultTokenService implements TokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtUtil jwtUtil;

    /**
     * Creates and stores a refresh token for the given user.
     * The token is saved to the database for later validation and tracking.
     *
     * @param userDetails The authenticated user's details.
     * @return A {@link Token} object representing the issued refresh token.
     */
    @Override
    @Transactional
    public Token createRefreshToken(WebUserDetails userDetails) {

        // Issue a refresh token
        Token issuedRefreshToken = issueRefreshToken(userDetails);

        // Build and save the refresh token entity
        RefreshToken token = RefreshToken
                .builder()
                .token(issuedRefreshToken.token())
                .createdAt(issuedRefreshToken.issuedAt())
                .expiresAt(issuedRefreshToken.expiresAt())
                .userId(userDetails.getId())
                .build();

        refreshTokenRepository.save(token);

        // Return the token data
        return new Token(token.getToken(), token.getCreatedAt(), token.getExpiresAt());
    }

    /**
     * Issues a refresh token for the given user without saving it to the database.
     * The token includes claims with user-specific information.
     *
     * @param userDetails The authenticated user's details.
     * @return A {@link Token} object representing the issued refresh token.
     */
    private Token issueRefreshToken(WebUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Add the user ID as the subject claim
        claims.put("sub", userDetails.getId().toString());

        // Issue the refresh token using a predefined lifetime
        return jwtUtil.issue(claims, JwtUtil.REFRESH_TOKEN_LIFETIME);
    }

    /**
     * Creates an access token for the given user.
     * The token contains user roles and other claims necessary for authorization.
     *
     * @param userDetails The authenticated user's details.
     * @return A {@link Token} object representing the issued access token.
     */
    @Override
    public Token createAccessToken(WebUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Extract user roles
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Add user claims to the token
        claims.put("sub", userDetails.getId().toString());
        claims.put("username", userDetails.getUsername());
        claims.put("roles", roles);

        // Issue the access token using a predefined lifetime
        return jwtUtil.issue(claims, JwtUtil.ACCESS_TOKEN_LIFETIME);
    }
}
