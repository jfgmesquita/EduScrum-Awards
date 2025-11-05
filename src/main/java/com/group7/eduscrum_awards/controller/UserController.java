package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.UserCreateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for managing Users.
 * Exposes endpoints for user registration and management.
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint to register a new User (Admin, Student, or Teacher).
     * Accessible via: POST http://localhost:8080/api/v1/users
     *
     * @param userCreateDTO The user data from the request body.
     * @return A ResponseEntity containing the created UserDTO and HTTP status 201.
     * @Valid triggers validation (@NotBlank, @Email, etc.) on the DTO
     * @RequestBody converts the JSON body into the UserCreateDTO object
     */
    @PostMapping
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserCreateDTO userCreateDTO) {
        
        UserDTO newUser = userService.registerUser(userCreateDTO);
        // Return the "safe" DTO (without password) and a 201 Created status
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}