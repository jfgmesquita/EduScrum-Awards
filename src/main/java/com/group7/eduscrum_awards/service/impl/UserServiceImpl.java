package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.UserCreateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.Teacher;
import com.group7.eduscrum_awards.model.User;
import com.group7.eduscrum_awards.repository.UserRepository;
import com.group7.eduscrum_awards.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

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
     * This implementation instantiates a User (Admin), Teacher, or Student based on the provided Role.
     *
     * @param userCreateDTO the DTO containing the registration data
     * @return a {@link UserDTO} representing the created user
     * @throws DuplicateResourceException if the email is already in use
     * @throws IllegalArgumentException if the role is null or invalid
     */
    @Override
    @Transactional
    public UserDTO registerUser(UserCreateDTO userCreateDTO) {
        
        // Validate Role
        Role role = userCreateDTO.getRole();

        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }

        // Check for duplicate email
        userRepository.findByEmail(userCreateDTO.getEmail())
            .ifPresent(user -> {
                throw new DuplicateResourceException("A user with the email '" + userCreateDTO.getEmail() + "' already exists.");
            });

        // Hash the password
        String hashedPassword = passwordEncoder.encode(userCreateDTO.getPassword());

        // Map DTO to the correct Entity based on Role
        User newUser;
        switch (role) {
            case ADMIN:
                newUser = new User(
                    userCreateDTO.getName(),
                    userCreateDTO.getEmail(),
                    hashedPassword,
                    Role.ADMIN
                );
                break;
            
            case TEACHER:
                newUser = new Teacher(
                    userCreateDTO.getName(),
                    userCreateDTO.getEmail(),
                    hashedPassword
                    // The Teacher constructor automatically sets Role.TEACHER
                );
                break;

            case STUDENT:
                newUser = new Student(
                    userCreateDTO.getName(),
                    userCreateDTO.getEmail(),
                    hashedPassword
                    // The Student constructor automatically sets Role.STUDENT
                );
                break;
            
            default:
                throw new IllegalArgumentException("Unsupported role: " + role);
        }

        // Save the new User (JPA will handle the user_type)
        User savedUser = userRepository.save(newUser);

        // Map Entity to Response DTO
        return new UserDTO(savedUser);
    }

    /**
     * Retrieves all students in the system.
     * 
     * @return A list of UserDTOs representing all students.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllStudents() {
        return userRepository.findAllByRole(Role.STUDENT).stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all teachers in the system.
     * 
     * @return A list of UserDTOs representing all teachers.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getAllTeachers() {
        return userRepository.findAllByRole(Role.TEACHER).stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }
}