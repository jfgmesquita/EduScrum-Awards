package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for receiving data when creating a new Task.
 * This object will be sent in the POST request body.
 */
@Getter
@Setter
public class TaskCreateDTO {

    @NotBlank(message = "Task description cannot be blank.")
    @Size(max = 10000, message = "Description must be less than 10000 characters.")
    private String description;
    
    // The sprintId will be in the URL.
}