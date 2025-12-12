package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.LoginRequestDTO;
import com.group7.eduscrum_awards.dto.LoginResponseDTO;
import com.group7.eduscrum_awards.model.User;
import com.group7.eduscrum_awards.repository.UserRepository;
import com.group7.eduscrum_awards.service.AuthService;
import com.group7.eduscrum_awards.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.HashMap;

/**
 * Implementation of the AuthService.
 * Handles the logic for user login and token generation.
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    /**
     * Authenticates a user.
     *
     * @param request The login DTO.
     * @return The response DTO with the token.
     */
    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {
        
        // Authenticate the user credentials
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.getEmail(),
                request.getPassword()
            )
        );

        // If authentication was successful, find the user
        User user = userRepository.findByEmail(request.getEmail())
            .orElseThrow(() -> new UsernameNotFoundException("User not found after successful authentication. This should not happen."));

        // Create any extra data to put in the token
        HashMap<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId());

        // Generate the JWT
        String jwtToken = jwtService.generateToken(claims, user);

        // Return the response DTO
        return new LoginResponseDTO(jwtToken, user.getId(), user.getEmail(), user.getRole().name());
    }
}