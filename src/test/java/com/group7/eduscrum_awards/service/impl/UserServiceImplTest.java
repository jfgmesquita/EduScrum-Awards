package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.StudentUpdateDTO;
import com.group7.eduscrum_awards.dto.TeacherUpdateDTO;
import com.group7.eduscrum_awards.dto.UserCreateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.Teacher;
import com.group7.eduscrum_awards.model.User;
import com.group7.eduscrum_awards.repository.CourseRepository;
import com.group7.eduscrum_awards.repository.DegreeRepository;
import com.group7.eduscrum_awards.repository.UserRepository;

import java.util.Optional;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private DegreeRepository degreeRepository;

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

    @Test
    @DisplayName("Should return list of all students")
    void testGetAllStudents() {

        Student student1 = new Student("S1", "s1@test.com", "pass");
        Student student2 = new Student("S2", "s2@test.com", "pass");
        List<User> mockStudents = Arrays.asList(student1, student2);

        when(userRepository.findAllByRole(Role.STUDENT)).thenReturn(mockStudents);

        List<UserDTO> result = userService.getAllStudents();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("S1", result.get(0).getName());
        verify(userRepository, times(1)).findAllByRole(Role.STUDENT);
    }

    @Test
    @DisplayName("Should return list of all teachers")
    void testGetAllTeachers() {

        Teacher teacher1 = new Teacher("T1", "t1@test.com", "pass");
        List<User> mockTeachers = Arrays.asList(teacher1);

        when(userRepository.findAllByRole(Role.TEACHER)).thenReturn(mockTeachers);

        List<UserDTO> result = userService.getAllTeachers();
        
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("T1", result.get(0).getName());
        verify(userRepository, times(1)).findAllByRole(Role.TEACHER);
    }

    @Test
    @DisplayName("updateStudent | Should update student details successfully")
    void testUpdateStudent_Success() {

        Long studentId = 1L;
        StudentUpdateDTO updateDTO = new StudentUpdateDTO();
        updateDTO.setName("New Name");
        updateDTO.setEmail("new@test.com");
        
        Student existingStudent = new Student("Old Name", "old@test.com", "pass");
        existingStudent.setId(studentId);
        
        when(userRepository.findById(studentId)).thenReturn(Optional.of(existingStudent));
        when(userRepository.save(any(Student.class))).thenAnswer(i -> i.getArguments()[0]); 
        UserDTO result = userService.updateStudent(studentId, updateDTO);

        assertEquals("New Name", result.getName());
        assertEquals("new@test.com", result.getEmail());
        verify(userRepository).save(existingStudent);
    }

    @Test
    @DisplayName("updatePassword | Should encode and save new password")
    void testUpdatePassword_Success() {

        Long userId = 1L;
        String newPass = "newPass123";
        User user = new User();
        user.setId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(passwordEncoder.encode(newPass)).thenReturn("encoded_pass");

        userService.updatePassword(userId, newPass);

        verify(passwordEncoder).encode(newPass);
        verify(userRepository).save(user);
    }

    @Test
    @DisplayName("getUserByEmail | Should return UserDTO when user exists")
    void testGetUserByEmail_Success() {

        String email = "existing@test.com";
        User user = new User("Existing User", email, "pass", Role.STUDENT);
        user.setId(5L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDTO result = userService.getUserByEmail(email);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    @DisplayName("getUserByEmail | Should throw EntityNotFoundException when user does not exist")
    void testGetUserByEmail_NotFound() {

        String email = "unknown@test.com";
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        jakarta.persistence.EntityNotFoundException exception = assertThrows(
            jakarta.persistence.EntityNotFoundException.class,
            () -> userService.getUserByEmail(email)
        );

        assertEquals("User not found", exception.getMessage());
        verify(userRepository, times(1)).findByEmail(email);
    }

    // Tests for getUserById

    @Test
    @DisplayName("getUserById | Should return UserDTO when found")
    void testGetUserById_Success() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(savedUser));

        UserDTO result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(savedUser.getEmail(), result.getEmail());
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("getUserById | Should throw ResourceNotFoundException when not found")
    void testGetUserById_NotFound() {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userService.getUserById(userId)
        );

        assertEquals("User not found: " + userId, exception.getMessage());
        verify(userRepository).findById(userId);
    }

    // Tests for updateTeacher

    @Test
    @DisplayName("updateTeacher | Should update name and email successfully")
    void testUpdateTeacher_Success() {
        Long teacherId = 2L;
        TeacherUpdateDTO updateDTO = new TeacherUpdateDTO();
        updateDTO.setName("Prof. Updated");
        updateDTO.setEmail("updated@prof.com");

        Teacher existingTeacher = new Teacher("Prof. Original", "original@prof.com", "pass");
        existingTeacher.setId(teacherId);

        when(userRepository.findById(teacherId)).thenReturn(Optional.of(existingTeacher));
        // Mock unique email check (empty means no other user has this email)
        when(userRepository.findByEmail("updated@prof.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(Teacher.class))).thenAnswer(i -> i.getArguments()[0]);

        UserDTO result = userService.updateTeacher(teacherId, updateDTO);

        assertNotNull(result);
        assertEquals("Prof. Updated", result.getName());
        assertEquals("updated@prof.com", result.getEmail());
        verify(userRepository).save(existingTeacher);
    }

    @Test
    @DisplayName("updateTeacher | Should throw DuplicateResourceException on duplicate email")
    void testUpdateTeacher_DuplicateEmail() {
        Long teacherId = 2L;
        TeacherUpdateDTO updateDTO = new TeacherUpdateDTO();
        updateDTO.setEmail("taken@test.com");

        Teacher existingTeacher = new Teacher("Prof. Test", "original@test.com", "pass");
        existingTeacher.setId(teacherId);

        // Another user already has this email
        User duplicateUser = new User();
        duplicateUser.setId(99L);

        when(userRepository.findById(teacherId)).thenReturn(Optional.of(existingTeacher));
        when(userRepository.findByEmail("taken@test.com")).thenReturn(Optional.of(duplicateUser));

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> userService.updateTeacher(teacherId, updateDTO)
        );

        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateTeacher | Should throw IllegalArgumentException if user is not a Teacher")
    void testUpdateTeacher_Failure_NotATeacher() {
        Long id = 1L;
        // Find a Student instead of a Teacher
        Student student = new Student("Student", "s@test.com", "pass");
        student.setId(id);
        
        when(userRepository.findById(id)).thenReturn(Optional.of(student));

        TeacherUpdateDTO dto = new TeacherUpdateDTO();
        dto.setName("New Name");

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> userService.updateTeacher(id, dto)
        );

        assertEquals("User is not a Teacher", exception.getMessage());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("updateTeacher | Should throw ResourceNotFoundException when user not found")
    void testUpdateTeacher_Failure_NotFound() {
        Long id = 99L;
        when(userRepository.findById(id)).thenReturn(Optional.empty());

        TeacherUpdateDTO dto = new TeacherUpdateDTO();

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userService.updateTeacher(id, dto)
        );

        assertEquals("User not found: " + id, exception.getMessage());
    }

    // Tests for getTeachersByCourse

    @Test
    @DisplayName("getTeachersByCourse | Should return list of teachers")
    void testGetTeachersByCourse_Success() {
        Long courseId = 10L;
        com.group7.eduscrum_awards.model.Course course = new com.group7.eduscrum_awards.model.Course("Math");
        course.setId(courseId);

        Teacher t1 = new Teacher("T1", "t1@test.com", "pass");
        course.getTeachers().add(t1);

        when(courseRepository.findById(courseId)).thenReturn(Optional.of(course));

        List<UserDTO> result = userService.getTeachersByCourse(courseId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("T1", result.get(0).getName());
        verify(courseRepository).findById(courseId);
    }

    @Test
    @DisplayName("getTeachersByCourse | Should throw ResourceNotFoundException when Course not found")
    void testGetTeachersByCourse_Failure_NotFound() {
        Long courseId = 99L;
        when(courseRepository.findById(courseId)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> userService.getTeachersByCourse(courseId)
        );

        assertEquals("Course not found: " + courseId, exception.getMessage());
    }
}