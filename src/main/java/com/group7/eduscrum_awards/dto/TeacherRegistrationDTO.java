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
    @Size(min = 3, max = 100)
    private String name;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email should be valid.")
    private String email;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 8)
    private String password;

    // Este campo é opcional (pode vir null se o frontend não selecionar nada)
    private Long courseIdToAssign; 
}