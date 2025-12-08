package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.TeacherRegistrationDTO;
import com.group7.eduscrum_awards.dto.UserCreateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.service.CourseService;
import com.group7.eduscrum_awards.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
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
    private final CourseService courseService;

    @Autowired
    public UserController(UserService userService, CourseService courseService) {
        this.userService = userService;
        this.courseService = courseService;
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

    /**
     * Endpoint to register a new Teacher and optionally assign to a Course.
     * Accessible via: POST http://localhost:8080/api/v1/users/teachers
     * 
     * @param dto The teacher registration data from the request body.
     * @return A ResponseEntity containing the created Teacher UserDTO and HTTP status 201.
     * @Valid triggers validation on the TeacherRegistrationDTO
     * @RequestBody converts the JSON body into the TeacherRegistrationDTO object
     */
    @PostMapping("/teachers")
    @Transactional // If anything fails, the whole operation is rolled back
    public ResponseEntity<UserDTO> registerTeacherWithCourse(@Valid @RequestBody TeacherRegistrationDTO dto) {
        
        // Map TeacherRegistrationDTO to UserCreateDTO
        UserCreateDTO userDto = new UserCreateDTO();
        userDto.setName(dto.getName());
        userDto.setEmail(dto.getEmail());
        userDto.setPassword(dto.getPassword());
        userDto.setRole(Role.TEACHER);

        // Register the Teacher user
        UserDTO createdTeacher = userService.registerUser(userDto);

        // If a courseIdToAssign was provided, assign the Teacher to that Course
        if (dto.getCourseIdToAssign() != null) {
            courseService.addTeacherToCourse(dto.getCourseIdToAssign(), createdTeacher.getId());
        }

        return new ResponseEntity<>(createdTeacher, HttpStatus.CREATED);
    }
}