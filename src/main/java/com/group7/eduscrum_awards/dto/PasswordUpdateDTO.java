package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/** Data Transfer Object for updating Password information. */
@Getter 
@Setter
public class PasswordUpdateDTO {
    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String newPassword;
}