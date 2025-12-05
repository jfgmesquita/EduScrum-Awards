package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for receiving data when a Teacher registers.
 * This object will be sent in the POST request body.
 */
@Getter
@Setter
public class TeacherRegistrationDTO {

    @NotBlank(message = "Name cannot be blank.")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters.")
    private String name;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email should be valid.")
    @Size(max = 100, message = "Email must be less than 100 characters.")
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 8, message = "Password must be at least 8 characters long.")
    private String password;

    // Este campo é opcional (pode vir null se o frontend não selecionar nada)
    private Long courseIdToAssign; 
}