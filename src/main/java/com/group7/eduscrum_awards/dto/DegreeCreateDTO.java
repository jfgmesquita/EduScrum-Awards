package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) used when creating a new {@link Degree}.
 * 
 * This object represents the payload expected from clients (Angular frontend)
 * when calling the REST endpoint that creates degrees.
 * Validation annotations are applied to ensure the incoming data is valid
 * before it reaches the service layer.
 */
@Getter
@Setter
public class DegreeCreateDTO {

    /**
     * The name of the degree.
     * 
     * Validation constraints:
     * {@link NotBlank} — must not be null or blank
     * {@link Size} — must be between 3 and 100 characters
     */
    @NotBlank(message = "The degree name cannot be blank.")
    @Size(min = 3, max = 100, message = "The name must be between 3 and 100 characters.")
    private String name;
}
