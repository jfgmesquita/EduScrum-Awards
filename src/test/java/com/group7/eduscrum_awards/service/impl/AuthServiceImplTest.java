package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.LoginRequestDTO;
import com.group7.eduscrum_awards.dto.LoginResponseDTO;
import com.group7.eduscrum_awards.model.User;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.repository.UserRepository;
import com.group7.eduscrum_awards.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.HashMap;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AuthServiceImpl.
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthServiceImpl authService;

    // Test data
    private LoginRequestDTO loginRequest;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Initialize test data
        loginRequest = new LoginRequestDTO();
        loginRequest.setEmail("admin@test.com");
        loginRequest.setPassword("password123");

        // Create a mock admin user
        adminUser = new User(
            "Admin User",
            "admin@test.com",
            "hashedpassword",
            Role.ADMIN
        );
        adminUser.setId(1L);
    }

    @Test
    @DisplayName("login | Should return LoginResponseDTO on successful authentication")
    void testLogin_Success() {
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
            loginRequest.getEmail(),
            loginRequest.getPassword()
        );
        
        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.of(adminUser));
        when(jwtService.generateToken(any(HashMap.class), eq(adminUser))).thenReturn("mock.jwt.token");
        
        LoginResponseDTO response = authService.login(loginRequest);

        assertNotNull(response);
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("ADMIN", response.getRole());
        assertEquals("admin@test.com", response.getEmail());

        verify(authenticationManager, times(1)).authenticate(authToken);
        verify(userRepository, times(1)).findByEmail("admin@test.com");
        verify(jwtService, times(1)).generateToken(any(HashMap.class), eq(adminUser));
    }

    @Test
    @DisplayName("login | Should throw AuthenticationException on bad credentials")
    void testLogin_Failure_BadCredentials() {
        
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
            .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThrows(AuthenticationException.class, () -> {
            authService.login(loginRequest);
        });

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, never()).findByEmail(anyString());
        verify(jwtService, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("login | Should throw UsernameNotFoundException if user not found after auth")
    void testLogin_Failure_UserNotFoundAfterAuth() {

        when(userRepository.findByEmail("admin@test.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            authService.login(loginRequest);
        });

        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository, times(1)).findByEmail("admin@test.com");
        verify(jwtService, never()).generateToken(any(), any());
    }
}