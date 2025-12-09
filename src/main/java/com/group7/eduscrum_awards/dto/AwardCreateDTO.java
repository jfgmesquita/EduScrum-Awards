package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** DTO for creating a custom Award within a Course. */
@Getter
@Setter
public class AwardCreateDTO {

    @NotBlank(message = "Award name cannot be blank.")
    @Size(min = 3, max = 50, message = "Name must be between 3 and 50 characters.")
    private String name;

    @Size(max = 255, message = "Description must be less than 255 characters.")
    private String description;

    @Min(value = 1, message = "Points must be at least 1.")
    @Max(value = 5, message = "Points cannot exceed 5.")
    private int points;
}