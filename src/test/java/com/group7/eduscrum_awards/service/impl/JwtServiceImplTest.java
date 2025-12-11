package com.group7.eduscrum_awards.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/** Unit tests for JwtServiceImpl. */
@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    @InjectMocks
    private JwtServiceImpl jwtService;

    @Mock
    private UserDetails userDetails;

    // Test secret key (256-bit hex string)
    private final String TEST_SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    @BeforeEach
    void setUp() {
        // Inject the test secret key into the jwtService
        ReflectionTestUtils.setField(jwtService, "jwtSecretKey", TEST_SECRET_KEY);
    }

    @Test
    @DisplayName("generateToken | Should generate a valid token")
    void testGenerateToken() {
        when(userDetails.getUsername()).thenReturn("user@test.com");

        String token = jwtService.generateToken(userDetails);

        assertNotNull(token);
        assertFalse(token.isEmpty());
        // Verify if the token contains the correct username
        assertEquals("user@test.com", jwtService.extractUsername(token));
    }

    @Test
    @DisplayName("generateToken | Should include extra claims")
    void testGenerateTokenWithExtraClaims() {
        when(userDetails.getUsername()).thenReturn("user@test.com");
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        claims.put("userId", 123);

        String token = jwtService.generateToken(claims, userDetails);

        Integer extractedId = jwtService.extractClaim(token, c -> c.get("userId", Integer.class));
        String extractedRole = jwtService.extractClaim(token, c -> c.get("role", String.class));

        assertEquals(123, extractedId);
        assertEquals("ADMIN", extractedRole);
    }

    @Test
    @DisplayName("isTokenValid | Should return true for valid user and non-expired token")
    void testIsTokenValid_Success() {
        when(userDetails.getUsername()).thenReturn("user@test.com");
        String token = jwtService.generateToken(userDetails);

        boolean isValid = jwtService.isTokenValid(token, userDetails);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("isTokenValid | Should return false for wrong username")
    void testIsTokenValid_WrongUser() {

        when(userDetails.getUsername()).thenReturn("user@test.com");
        String token = jwtService.generateToken(userDetails);

        UserDetails otherUser = mock(UserDetails.class);
        when(otherUser.getUsername()).thenReturn("other@test.com");

        boolean isValid = jwtService.isTokenValid(token, otherUser);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("isTokenExpired | Should return false for a newly created token")
    void testIsTokenExpired() {
        when(userDetails.getUsername()).thenReturn("user@test.com");
        String token = jwtService.generateToken(userDetails);

        boolean isExpired = jwtService.isTokenExpired(token);

        assertFalse(isExpired);
    }
}