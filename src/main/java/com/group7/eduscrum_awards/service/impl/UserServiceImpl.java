package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.UserCreateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.model.User;
import com.group7.eduscrum_awards.repository.UserRepository;
import com.group7.eduscrum_awards.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing User entities.
 * Implements the {@link UserService} contract.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Registers a new user, performing validation and password hashing.
     *
     * @param userCreateDTO the DTO containing the registration data
     * @return a {@link UserDTO} representing the created user
     * @throws DuplicateResourceException if the email is already in use
     */
    @Override
    @Transactional
    public UserDTO registerUser(UserCreateDTO userCreateDTO) {
        
        // Check for duplicate email
        userRepository.findByEmail(userCreateDTO.getEmail())
            .ifPresent(user -> {
                throw new DuplicateResourceException("A user with the email '" + userCreateDTO.getEmail() + "' already exists.");
            });

        // Hash the password
        String hashedPassword = passwordEncoder.encode(userCreateDTO.getPassword());

        // Map DTO to Entity
        User newUser = new User(
            userCreateDTO.getName(),
            userCreateDTO.getEmail(),
            hashedPassword,
            userCreateDTO.getRole()
        );

        // Save the new User
        User savedUser = userRepository.save(newUser);

        // Map Entity to Response DTO
        return new UserDTO(savedUser);
    }
}