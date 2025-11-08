package com.group7.eduscrum_awards.service;

import org.springframework.security.core.userdetails.UserDetails;
import java.util.Map;
import java.util.function.Function;
import io.jsonwebtoken.Claims;

/**
 * Service Interface (Contract) for handling JSON Web Tokens (JWT).
 * Defines operations for creating, validating, and extracting information from tokens.
 */
public interface JwtService {

    /**
     * Extracts the username (email) from the JWT.
     * @param token The JWT string.
     * @return The username (email).
     */
    String extractUsername(String token);

    /**
     * Generates a new JWT for a user.
     * @param userDetails The user's details.
     * @return A new JWT string.
     */
    String generateToken(UserDetails userDetails);

    /**
     * Generates a new JWT with extra claims.
     * @param extraClaims Extra information to put in the token (e.g., role).
     * @param userDetails The user's details.
     * @return A new JWT string.
     */
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    /**
     * Checks if a token is valid (i.e., not expired and signed by us).
     * @param token The JWT string.
     * @param userDetails The user's details to validate against.
     * @return true if the token is valid, false otherwise.
     */
    boolean isTokenValid(String token, UserDetails userDetails);

    /**
     * Checks if a token has expired.
     * @param token The JWT string.
     * @return true if the token is expired, false otherwise.
     */
    boolean isTokenExpired(String token);

    /**
     * A generic function to extract a specific "claim" (piece of data) from the token.
     * @param token The JWT string.
     * @param claimsResolver A function to extract the desired claim.
     * @return The claim value.
     */
    <T> T extractClaim(String token, Function<Claims, T> claimsResolver);
}