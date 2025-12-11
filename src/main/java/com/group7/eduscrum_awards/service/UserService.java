package com.group7.eduscrum_awards.service;

import java.util.List;

import com.group7.eduscrum_awards.dto.UserCreateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;

/**
 * Service Interface (Contract) for User operations.
 * Defines business logic methods for managing users.
 */
public interface UserService {

    /**
     * Registers a new user in the system.
     * This method handles email duplication checks and password hashing.
     *
     * @param userCreateDTO The DTO containing the data for the new User.
     * @return The DTO of the newly created User (without the password).
     * @throws DuplicateResourceException if the email already exists.
     */
    UserDTO registerUser(UserCreateDTO userCreateDTO);

    /**
     * Retrieves all students in the system.
     * 
     * @return A list of UserDTOs representing all students.
     */
    List<UserDTO> getAllStudents();
    
    /**
     * Retrieves all teachers in the system.
     * 
     * @return A list of UserDTOs representing all teachers.
     */
    List<UserDTO> getAllTeachers();
}