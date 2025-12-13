package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.StudentUpdateDTO;
import com.group7.eduscrum_awards.dto.TeacherUpdateDTO;
import com.group7.eduscrum_awards.dto.UserCreateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Degree;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.Teacher;
import com.group7.eduscrum_awards.model.User;
import com.group7.eduscrum_awards.repository.CourseRepository;
import com.group7.eduscrum_awards.repository.DegreeRepository;
import com.group7.eduscrum_awards.repository.UserRepository;
import com.group7.eduscrum_awards.service.UserService;

import jakarta.persistence.EntityNotFoundException;

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
    private final CourseRepository courseRepository;
    private final DegreeRepository degreeRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, DegreeRepository degreeRepository, 
        CourseRepository courseRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.degreeRepository = degreeRepository;
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

    /**
     * Updates an existing student's information.
     *
     * @param id the ID of the student to update
     * @param dto the DTO containing the updated information
     * @return a {@link UserDTO} representing the updated student
     * @throws ResourceNotFoundException when no user with the given ID exists
     * @throws IllegalArgumentException when the user is not a student
     */
    @Override
    @Transactional
    public UserDTO updateStudent(Long id, StudentUpdateDTO dto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));

        if (!(user instanceof Student)) throw new IllegalArgumentException("User is not a student");
        Student student = (Student) user;

        if (dto.getName() != null) student.setName(dto.getName());
        if (dto.getEmail() != null) student.setEmail(dto.getEmail());
        if (dto.getDegreeId() != null) {
            Degree degree = degreeRepository.findById(dto.getDegreeId())
                .orElseThrow(() -> new ResourceNotFoundException("Degree not found"));
            student.setDegree(degree);
        }
        return new UserDTO(userRepository.save(student));
    }

    /**
     * Updates the password of an existing user.
     *
     * @param userId the ID of the user whose password is to be updated
     * @param newPassword the new password to set
     * @throws ResourceNotFoundException when no user with the given ID exists
     */
    @Override
    @Transactional
    public void updatePassword(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepository.save(user);
        }
    }

    /**
     * Retrieves a user by their username.
     *
     * @param email the email of the user to retrieve
     * @return a {@link UserDTO} representing the found user
     * @throws EntityNotFoundException when no user with the given username exists
     */
    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        return new UserDTO(user);
    }

    /**
     * Retrieves a user by their ID.
     *
     * @param id the ID of the user to retrieve
     * @return a {@link UserDTO} representing the found user
     * @throws ResourceNotFoundException when no user with the given ID exists
     */
    @Override
    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        return new UserDTO(user);
    }

    /**
     * Updates an existing teacher's information.
     *
     * @param id the ID of the teacher to update
     * @param dto the DTO containing the updated information
     * @return a {@link UserDTO} representing the updated teacher
     * @throws ResourceNotFoundException when no user with the given ID exists
     * @throws IllegalArgumentException when the user is not a teacher
     */
    @Override
    @Transactional
    public UserDTO updateTeacher(Long id, TeacherUpdateDTO dto) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
        
        if (!(user instanceof Teacher)) {
            throw new IllegalArgumentException("User is not a Teacher");
        }

        Teacher teacher = (Teacher) user;

        if (dto.getName() != null && !dto.getName().isBlank()) {
            teacher.setName(dto.getName());
        }
        
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            // Check uniqueness if email changed
            if (!teacher.getEmail().equals(dto.getEmail())) {
                userRepository.findByEmail(dto.getEmail()).ifPresent(u -> {
                    throw new DuplicateResourceException("Email already in use");
                });
                teacher.setEmail(dto.getEmail());
            }
        }
        
        return new UserDTO(userRepository.save(teacher));
    }

    /**
     * Retrieves all teachers teaching a specific course.
     * 
     * @param courseId The ID of the course.
     * @return A list of {@link UserDTO} representing the teachers of the specified course.
     * @throws ResourceNotFoundException if the course is not found.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserDTO> getTeachersByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
        
        return course.getTeachers().stream()
                .map(UserDTO::new)
                .collect(Collectors.toList());
    }
}