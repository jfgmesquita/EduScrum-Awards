package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.LoginRequestDTO;
import com.group7.eduscrum_awards.dto.LoginResponseDTO;
import com.group7.eduscrum_awards.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** REST Controller for Authentication operations (e.g., login). */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Endpoint for user login.
     * Accessible via: POST /api/v1/auth/login
     *
     * @param request The login request body (email and password).
     * @return A ResponseEntity containing the LoginResponseDTO (with the JWT).
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {

        LoginResponseDTO response = authService.login(request);        
        // Return 200 OK with the token in the body
        return ResponseEntity.ok(response);
    }
}