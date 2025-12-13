package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for updating Teacher information.
 * Fields are optional (can be null) to allow partial updates.
 */
@Getter
@Setter
public class TeacherUpdateDTO {

    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters.")
    private String name;

    @Email(message = "Invalid email format.")
    private String email;
}