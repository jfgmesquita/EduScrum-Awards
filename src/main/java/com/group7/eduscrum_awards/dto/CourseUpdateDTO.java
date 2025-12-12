package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/** Data Transfer Object for updating Course information. */
@Getter
@Setter
public class CourseUpdateDTO {
    @NotBlank(message = "The course name cannot be blank.")
    @Size(min = 3, max = 100, message = "The name must be between 3 and 100 characters.")
    private String name;
}