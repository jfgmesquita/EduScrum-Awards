package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

/** Data Transfer Object for updating Student information. */
@Getter 
@Setter
public class StudentUpdateDTO {

    @NotBlank(message = "Name cannot be blank.")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters.")
    private String name;

    @NotBlank(message = "Email cannot be blank.")
    @Email(message = "Email should be valid.")
    @Size(max = 100, message = "Email must be less than 100 characters.")
    private String email;

    private Long degreeId; // Optional: to update the associated degree
}