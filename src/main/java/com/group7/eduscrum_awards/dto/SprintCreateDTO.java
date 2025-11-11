package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

/**
 * DTO for receiving data when creating a new Sprint.
 * This object will be sent in the POST request body.
 */
@Getter
@Setter
public class SprintCreateDTO {

    @NotNull(message = "Sprint number is required.")
    @Min(value = 1, message = "Sprint number must be at least 1.")
    private Integer sprintNumber;

    @NotBlank(message = "Final goal cannot be blank.")
    private String finalGoal;

    @NotNull(message = "Start date is required.")
    private LocalDate startDate;

    @NotNull(message = "End date is required.")
    private LocalDate endDate;
    
    // The projectId will be in the URL path, not the body
}