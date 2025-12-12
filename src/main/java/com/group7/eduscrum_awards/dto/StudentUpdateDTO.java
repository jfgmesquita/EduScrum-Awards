package com.group7.eduscrum_awards.dto;
import lombok.Getter;
import lombok.Setter;

/** Data Transfer Object for updating Student information. */
@Getter 
@Setter
public class StudentUpdateDTO {
    private String name;
    private String email;
    private Long degreeId; // Opcional: to update the associated degree
}