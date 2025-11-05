package com.group7.eduscrum_awards.service;

import com.group7.eduscrum_awards.dto.UserCreateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;

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

    // Future methods:
    // UserDTO loginUser(LoginDTO loginDTO);
    // UserDTO getUserByEmail(String email);
    // List<UserDTO> getAllUsers();
}