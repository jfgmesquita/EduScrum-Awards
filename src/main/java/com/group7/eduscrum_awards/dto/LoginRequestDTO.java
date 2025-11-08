package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/** DTO for receiving login credentials (request body). */
@Getter
@Setter
public class LoginRequestDTO {

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    private String password;
}