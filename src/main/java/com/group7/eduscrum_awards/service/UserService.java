package com.group7.eduscrum_awards.service;

import java.util.List;

import com.group7.eduscrum_awards.dto.StudentUpdateDTO;
import com.group7.eduscrum_awards.dto.UserCreateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;

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

    /**
     * Updates an existing student's information.
     *
     * @param id the ID of the student to update
     * @param dto the DTO containing the updated information
     * @return a {@link UserDTO} representing the updated student
     * @throws ResourceNotFoundException when no user with the given ID exists
     * @throws IllegalArgumentException when the user is not a student
     */
    public UserDTO updateStudent(Long id, StudentUpdateDTO dto);

    /**
     * Updates the password of an existing user.
     *
     * @param userId the ID of the user whose password is to be updated
     * @param newPassword the new password to set
     * @throws ResourceNotFoundException when no user with the given ID exists
     */
    public void updatePassword(Long userId, String newPassword);

    /**
     * Retrieves a user by their username.
     *
     * @param email the email of the user to retrieve
     * @return a {@link UserDTO} representing the found user
     * @throws EntityNotFoundException when no user with the given email exists
     */
    public UserDTO getUserByEmail(String email);
}