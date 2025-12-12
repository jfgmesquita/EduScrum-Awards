package com.group7.eduscrum_awards.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/** Data Transfer Object for updating Degree information. */
@Getter
@Setter
public class DegreeUpdateDTO {
    @NotBlank(message = "The degree name cannot be blank.")
    @Size(min = 3, max = 100, message = "The name must be between 3 and 100 characters.")
    private String name;
}