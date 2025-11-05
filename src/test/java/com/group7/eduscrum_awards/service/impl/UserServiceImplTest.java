package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.UserCreateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.Teacher;
import com.group7.eduscrum_awards.model.User;
import com.group7.eduscrum_awards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the UserServiceImpl.
 * These tests isolate the service logic and mock the 
 * UserRepository and PasswordEncoder dependencies.
 */
@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private UserCreateDTO createDTO;
    private User savedUser;
    private Teacher savedTeacher;
    private Student savedStudent;
    private String hashedPassword;

    /** Sets up common test data before each test. */
    @BeforeEach
    void setUp() {
        // Simulate the DTO coming from the controller
        createDTO = new UserCreateDTO();
        createDTO.setName("Test User");
        createDTO.setEmail("test@user.com");
        createDTO.setPassword("plainpassword123");
        createDTO.setRole(Role.ADMIN);

        // Simulate the hashed password
        hashedPassword = "bcrypt-hashed-password-string";

        // Simulate the User entity as it would be saved in the DB
        savedUser = new User(
            createDTO.getName(),
            createDTO.getEmail(),
            hashedPassword,
            createDTO.getRole()
        );
        savedUser.setId(1L);
    }

    @Test
    @DisplayName("Should register user successfully when email is unique")
    void testRegisterUser_Success() {

        when(userRepository.findByEmail("test@user.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainpassword123")).thenReturn(hashedPassword);
        doReturn(savedUser).when(userRepository).save(notNull());

        UserDTO result = userService.registerUser(createDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getName());
        assertEquals("test@user.com", result.getEmail());
        assertEquals(Role.ADMIN, result.getRole());

        verify(userRepository, times(1)).findByEmail("test@user.com");
        verify(passwordEncoder, times(1)).encode("plainpassword123");
        verify(userRepository, times(1)).save(notNull());
    }

    @Test
    @DisplayName("Should throw DuplicateResourceException when email already exists")
    void testRegisterUser_Failure_DuplicateEmail() {

        when(userRepository.findByEmail("test@user.com")).thenReturn(Optional.of(savedUser));

        DuplicateResourceException exceptionThrown = assertThrows(
            DuplicateResourceException.class,
            () -> {
                userService.registerUser(createDTO);
            }
        );
        assertEquals("A user with the email 'test@user.com' already exists.", exceptionThrown.getMessage());

        verify(userRepository, times(1)).findByEmail("test@user.com");
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should register successfully when Role is TEACHER")
    void testRegisterUser_Success_AsTeacher() {
        createDTO.setRole(Role.TEACHER);
        createDTO.setEmail("teacher@test.com");
        
        savedTeacher = new Teacher(
            createDTO.getName(),
            createDTO.getEmail(),
            hashedPassword
        );
        savedTeacher.setId(2L);

        when(userRepository.findByEmail("teacher@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(createDTO.getPassword())).thenReturn(hashedPassword);
        when(userRepository.save(any(Teacher.class))).thenReturn(savedTeacher);

        UserDTO result = userService.registerUser(createDTO);

        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals(Role.TEACHER, result.getRole());
        
        verify(userRepository, times(1)).findByEmail("teacher@test.com");
        verify(passwordEncoder, times(1)).encode(createDTO.getPassword());
        verify(userRepository, times(1)).save(any(Teacher.class));
    }

    @Test
    @DisplayName("Should register successfully when Role is STUDENT")
    void testRegisterUser_Success_AsStudent() {
        createDTO.setRole(Role.STUDENT);
        createDTO.setEmail("student@test.com");
        
        savedStudent = new Student(
            createDTO.getName(),
            createDTO.getEmail(),
            hashedPassword
        );
        savedStudent.setId(3L);

        when(userRepository.findByEmail("student@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(createDTO.getPassword())).thenReturn(hashedPassword);
        when(userRepository.save(any(Student.class))).thenReturn(savedStudent);

        UserDTO result = userService.registerUser(createDTO);

        assertNotNull(result);
        assertEquals(3L, result.getId());
        assertEquals(Role.STUDENT, result.getRole());
        
        verify(userRepository, times(1)).findByEmail("student@test.com");
        verify(passwordEncoder, times(1)).encode(createDTO.getPassword());
        verify(userRepository, times(1)).save(any(Student.class));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when Role is null")
    void testRegisterUser_Failure_NullRole() {
        createDTO.setRole(null);

        IllegalArgumentException exceptionThrown = assertThrows(
            IllegalArgumentException.class,
            () -> {
                userService.registerUser(createDTO);
            }
        );

        assertEquals("Role cannot be null.", exceptionThrown.getMessage());
        
        verify(userRepository, never()).findByEmail(anyString());
        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}