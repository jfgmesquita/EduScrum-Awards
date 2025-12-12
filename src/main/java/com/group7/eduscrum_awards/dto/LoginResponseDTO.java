package com.group7.eduscrum_awards.dto;

import lombok.Getter;

/** DTO for sending a successful login response (a JWT token). */
@Getter
public class LoginResponseDTO {

    private String token;
    private Long id;
    private String email;
    private String role;

    public LoginResponseDTO(String token, Long id, String email, String role) {
        this.token = token;
        this.id = id;
        this.email = email;
        this.role = role;
    }
} 