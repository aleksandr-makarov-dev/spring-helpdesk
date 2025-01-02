package com.aleksandrmakarovdev.helpdesk.user.util;

import com.aleksandrmakarovdev.helpdesk.security.WebUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.function.Function;

@Service
public class JwtUtil {

    @Value("{jwt.access-token.secret}")
    private String secret;

    @Value("{jwt.access-token.lifetime}")
    private Duration lifetime;

    /**
     * Generates the SecretKey used for signing and verifying JWT tokens.
     * The key is derived from the secret specified in the configuration.
     *
     * @return SecretKey for HMAC-SHA-based JWT operations.
     */
    private SecretKey getKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Extracts the roles from the JWT claims.
     *
     * TODO: Make this method typesafe to avoid potential runtime type issues.
     *
     * @param claims JWT claims from which roles are to be extracted.
     * @return List of roles extracted from the claims.
     */
    private List<String> extractRoles(Claims claims) {
        return claims.get("roles", List.class);
    }

    /**
     * Parses and retrieves all claims from a JWT token.
     *
     * @param token The JWT token to parse.
     * @return Claims object containing all claims from the token.
     */
    private Claims getlAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Retrieves a specific claim from a JWT token using a resolver function.
     *
     * @param <T>            The type of the claim to be extracted.
     * @param token          The JWT token to parse.
     * @param claimsResolver A function to resolve and extract the desired claim from the claims.
     * @return The extracted claim of type T.
     */
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getlAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Retrieves the username (subject) from a JWT token.
     *
     * @param token The JWT token to parse.
     * @return The username extracted from the token.
     */
    public String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    /**
     * Retrieves the expiration date from a JWT token.
     *
     * @param token The JWT token to parse.
     * @return The expiration date of the token.
     */
    public Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    /**
     * Retrieves the roles from a JWT token.
     *
     * @param token The JWT token to parse.
     * @return List of roles extracted from the token.
     */
    public List<String> getRoles(String token) {
        return getClaim(token, this::extractRoles);
    }

    /**
     * Issues a new JWT token for the given user details.
     *
     * @param userDetails The details of the user for whom the token is being issued.
     * @return A JWT token string containing user information and roles.
     */
    public String issueToken(WebUserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        claims.put("username", userDetails.getUsername());
        claims.put("roles", roles);

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claims(claims)
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(lifetime)))
                .signWith(getKey(), Jwts.SIG.HS256)
                .compact();
    }
}

