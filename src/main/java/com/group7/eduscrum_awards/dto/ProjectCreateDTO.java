package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

/**
 * DTO for receiving data when a Teacher creates a new Project.
 * This object will be sent in the POST request body.
 */
@Getter
@Setter
public class ProjectCreateDTO {

    @NotBlank(message = "Project name cannot be blank.")
    @Size(min = 3, max = 100, message = "Project name must be between 3 and 100 characters.")
    private String name;

    @Size(max = 5000, message = "Description must be less than 5000 characters.")
    private String description;

    // Dates can be optional at creation
    private LocalDate startDate;
    private LocalDate endDate;
}