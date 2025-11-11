package com.group7.eduscrum_awards.service;

import com.group7.eduscrum_awards.dto.LoginRequestDTO;
import com.group7.eduscrum_awards.dto.LoginResponseDTO;

/** Service Interface for Authentication operations. */
public interface AuthService {

    /**
     * Authenticates a user and returns a JWT.
     *
     * @param request The login request containing email and password.
     * @return A response DTO containing the JWT and user info.
     * @throws org.springframework.security.core.AuthenticationException if credentials are invalid.
     */
    LoginResponseDTO login(LoginRequestDTO request);
}