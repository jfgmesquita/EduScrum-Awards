package com.group7.eduscrum_awards.dto;

import com.group7.eduscrum_awards.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for receiving data when creating a new User.
 * This class includes validation rules for registration.
 */
@Getter
@Setter
public class UserCreateDTO {

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

    @NotNull(message = "Role cannot be null.")
    private Role role; // This will be Role.ADMIN, Role.STUDENT or Role.TEACHER
}