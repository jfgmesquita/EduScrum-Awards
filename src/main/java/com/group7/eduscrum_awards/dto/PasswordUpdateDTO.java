package com.group7.eduscrum_awards.dto;
import lombok.Getter;
import lombok.Setter;

/** Data Transfer Object for updating Password information. */
@Getter 
@Setter
public class PasswordUpdateDTO {
    private String newPassword;
}