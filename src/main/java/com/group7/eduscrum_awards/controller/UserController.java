package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.PasswordUpdateDTO;
import com.group7.eduscrum_awards.dto.StudentUpdateDTO;
import com.group7.eduscrum_awards.dto.TeacherRegistrationDTO;
import com.group7.eduscrum_awards.dto.TeacherUpdateDTO;
import com.group7.eduscrum_awards.dto.UserCreateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.service.CourseService;
import com.group7.eduscrum_awards.service.UserService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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
     * Endpoint to retrieve a User by their ID.
     * Accessible via: GET /api/v1/users/{id}
     * 
     * @param id The ID of the User to retrieve.
     * @return A ResponseEntity containing the UserDTO and HTTP status 200.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Endpoint to update an existing Teacher.
     * Accessible via: PUT /api/v1/users/teachers/{id}
     * 
     * @param id The ID of the Teacher to be updated.
     * @param dto The updated data for the Teacher, passed in the request body.
     * @return A ResponseEntity containing the updated UserDTO and HTTP status 200.
     */
    @PutMapping("/teachers/{id}")
    public ResponseEntity<UserDTO> updateTeacher(@PathVariable Long id, @Valid @RequestBody TeacherUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateTeacher(id, dto));
    }

    /**
     * Endpoint to retrieve all Teachers teaching a specific Course.
     * Accessible via: GET /api/v1/users/courses/{courseId}/teachers
     * 
     * @param courseId The ID of the Course.
     * @return A ResponseEntity containing the list of UserDTOs and HTTP status 200.
     */
    @GetMapping("/courses/{courseId}/teachers")
    public ResponseEntity<List<UserDTO>> getTeachersByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(userService.getTeachersByCourse(courseId));
    }

    /**
     * Endpoint to register a new User (Admin, Student, or Teacher).
     * Accessible via: POST /api/v1/users
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
     * Accessible via: POST /api/v1/users/teachers
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

    /**
     * Get all students.
     * Accessible via: GET /api/v1/users/students
     * 
     * @return A ResponseEntity containing a list of UserDTOs representing all students.
     */
    @GetMapping("/students")
    public ResponseEntity<List<UserDTO>> getAllStudents() {
        return ResponseEntity.ok(userService.getAllStudents());
    }

    /**
     * Get all teachers.
     * Accessible via: GET /api/v1/users/teachers
     * 
     * @return A ResponseEntity containing a list of UserDTOs representing all teachers.
     */
    @GetMapping("/teachers")
    public ResponseEntity<List<UserDTO>> getAllTeachers() {
        return ResponseEntity.ok(userService.getAllTeachers());
    }

    /**
     * Endpoint to update an existing Student.
     * Accessible via: PUT /api/v1/users/students/{id}
     * 
     * @param id The ID of the Student to be updated.
     * @param dto The updated data for the Student, passed in the request body.
     * @return A ResponseEntity containing the updated UserDTO and HTTP status 200.
     */
    @PutMapping("/students/{id}")
    public ResponseEntity<UserDTO> updateStudent(@PathVariable Long id, @RequestBody StudentUpdateDTO dto) {
        return ResponseEntity.ok(userService.updateStudent(id, dto));
    }

    /**
     * Endpoint to update a User's password.
     * Accessible via: PATCH /api/v1/users/{id}/password
     * 
     * @param id The ID of the User whose password is to be updated.
     * @param dto The PasswordUpdateDTO containing the new password.
     * @return A ResponseEntity with HTTP status 204 (No Content) upon successful update.
     */
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody PasswordUpdateDTO dto) {
        userService.updatePassword(id, dto.getNewPassword());
        return ResponseEntity.noContent().build();
    }
}