package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating Teacher information.
 * Contains fields that can be updated for a Teacher.
 */
@Getter
@Setter
public class TeacherUpdateDTO {

    @NotBlank(message = "Name cannot be blank.")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters.")
    private String name;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Invalid email format.")
    private String email;
}