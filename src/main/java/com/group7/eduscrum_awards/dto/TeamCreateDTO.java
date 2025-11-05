package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for receiving data when a Teacher creates a new Team.
 * This object will be sent in the POST request body.
 */
@Getter
@Setter
public class TeamCreateDTO {

    @NotBlank(message = "Team name cannot be blank.")
    @Size(min = 3, max = 100, message = "Team name must be between 3 and 100 characters.")
    private String name;

    @NotNull(message = "Group number is required.")
    @Min(value = 1, message = "Group number must be at least 1.")
    private Integer groupNumber;
}