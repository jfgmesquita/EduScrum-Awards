package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO (Data Transfer Object) used when creating a new {@link Course}.
 * This object represents the payload expected from clients
 * when calling the REST endpoint that creates courses.
 */
@Getter
@Setter
public class CourseCreateDTO {
    @NotBlank(message = "The course name cannot be blank.")
    @Size(min = 3, max = 100, message = "The name must be between 3 and 100 characters.")
    private String name;
}