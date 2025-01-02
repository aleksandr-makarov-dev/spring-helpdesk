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

    @Override
    @Transactional
    public Token createRefreshToken(WebUserDetails userDetails) {

        Token issuedRefreshToken = issueRefreshToken(userDetails);

        RefreshToken token = RefreshToken
                .builder()
                .token(issuedRefreshToken.token())
                .createdAt(issuedRefreshToken.issuedAt())
                .expiresAt(issuedRefreshToken.expiresAt())
                .userId(userDetails.getId())
                .build();

        refreshTokenRepository.save(token);

        return new Token(token.getToken(), token.getCreatedAt(), token.getExpiresAt());
    }

    private Token issueRefreshToken(WebUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        claims.put("sub", userDetails.getId().toString());

        return jwtUtil.issue(claims, JwtUtil.REFRESH_TOKEN_LIFETIME);
    }

    @Override
    public Token createAccessToken(WebUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        claims.put("sub", userDetails.getId().toString());
        claims.put("username", userDetails.getUsername());
        claims.put("roles", roles);

        return jwtUtil.issue(claims, JwtUtil.ACCESS_TOKEN_LIFETIME);
    }
}
