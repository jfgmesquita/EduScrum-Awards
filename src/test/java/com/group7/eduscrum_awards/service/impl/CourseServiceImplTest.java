package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.CourseCreateDTO;
import com.group7.eduscrum_awards.dto.CourseDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Degree;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.Teacher;
import com.group7.eduscrum_awards.model.User;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.repository.CourseRepository;
import com.group7.eduscrum_awards.repository.DegreeRepository;
import com.group7.eduscrum_awards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the CourseServiceImpl.
 * These tests isolate the service layer logic and use
 * Mockito to simulate the behavior of the CourseRepository.
 */
@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;
    
    @Mock
    private DegreeRepository degreeRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    // Test Data
    private Degree existingDegree;
    private CourseCreateDTO createDTO;
    private Course existingCourse;
    private Teacher existingTeacher;
    private Student existingStudent;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Data for registerCourseForDegree tests
        existingDegree = new Degree("Test Degree");
        existingDegree.setId(1L);

        createDTO = new CourseCreateDTO();
        createDTO.setName("Test Course");

        // Use the real HashSet from the entity
        existingCourse = new Course("Test Course");
        existingCourse.setId(99L);
        existingCourse.setDegree(existingDegree);

        // Data for addTeacherToCourse tests
        existingTeacher = new Teacher("Prof. Teste", "prof@teste.com", "hashedpass");
        existingTeacher.setId(3L);
        
        // Data for addStudentToCourse tests
        existingStudent = new Student("Aluno Teste", "aluno@teste.com", "hashedpass");
        existingStudent.setId(5L);

        // Data to test user_is_not_a_teacher
        adminUser = new User("Admin", "admin@teste.com", "pass", Role.ADMIN);
        adminUser.setId(4L);
    }

    // Tests for registerCourseForDegree

    @Test
    @DisplayName("registerCourse | Should register successfully when name is unique")
    void testRegisterCourseForDegree_Success() {

        when(degreeRepository.findById(1L)).thenReturn(Optional.of(existingDegree));
        when(courseRepository.findByNameAndDegree("Test Course", existingDegree)).thenReturn(Optional.empty());
        doReturn(existingCourse).when(courseRepository).save(notNull());
        CourseDTO result = courseService.registerCourseForDegree(1L, createDTO);
        assertNotNull(result);
        assertEquals(99L, result.getId());
    }

    @Test
    @DisplayName("registerCourse | Should throw ResourceNotFoundException when Degree not found")
    void testRegisterCourseForDegree_Failure_DegreeNotFound() {

        when(degreeRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.registerCourseForDegree(1L, createDTO);
        });
    }

    @Test
    @DisplayName("registerCourse | Should throw DuplicateResourceException when name exists in Degree")
    void testRegisterCourseForDegree_Failure_DuplicateName() {

        when(degreeRepository.findById(1L)).thenReturn(Optional.of(existingDegree));
        when(courseRepository.findByNameAndDegree("Test Course", existingDegree)).thenReturn(Optional.of(existingCourse));
        assertThrows(DuplicateResourceException.class, () -> {
            courseService.registerCourseForDegree(1L, createDTO);
        });
    }

    // Tests for addTeacherToCourse

    @Test
    @DisplayName("addTeacherToCourse | Should add teacher successfully")
    void testAddTeacherToCourse_Success() {

        when(courseRepository.findById(99L)).thenReturn(Optional.of(existingCourse));
        when(userRepository.findById(3L)).thenReturn(Optional.of(existingTeacher));
        CourseDTO result = courseService.addTeacherToCourse(99L, 3L);
        assertNotNull(result);
        assertTrue(existingCourse.getTeachers().contains(existingTeacher));
        verify(courseRepository, times(1)).save(existingCourse);
    }

    @Test
    @DisplayName("addTeacherToCourse | Should throw ResourceNotFoundException when Course not found")
    void testAddTeacherToCourse_Failure_CourseNotFound() {

        when(courseRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.addTeacherToCourse(99L, 3L);
        });
    }

    @Test
    @DisplayName("addTeacherToCourse | Should throw ResourceNotFoundException when Teacher not found")
    void testAddTeacherToCourse_Failure_TeacherNotFound() {

        when(courseRepository.findById(99L)).thenReturn(Optional.of(existingCourse));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.addTeacherToCourse(99L, 3L);
        });
    }

    @Test
    @DisplayName("addTeacherToCourse | Should throw ResourceNotFoundException when User is not a Teacher")
    void testAddTeacherToCourse_Failure_UserIsNotATeacher() {

        when(courseRepository.findById(99L)).thenReturn(Optional.of(existingCourse));
        when(userRepository.findById(4L)).thenReturn(Optional.of(adminUser));
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            courseService.addTeacherToCourse(99L, 4L);
        });
        assertEquals("Teacher not found with id: 4", exception.getMessage());
    }

    @Test
    @DisplayName("addTeacherToCourse | Should throw DuplicateResourceException when Teacher already assigned")
    void testAddTeacherToCourse_Failure_AlreadyAssigned() {

        existingCourse.getTeachers().add(existingTeacher);
        when(courseRepository.findById(99L)).thenReturn(Optional.of(existingCourse));
        when(userRepository.findById(3L)).thenReturn(Optional.of(existingTeacher));
        assertThrows(DuplicateResourceException.class, () -> {
            courseService.addTeacherToCourse(99L, 3L);
        });
    }


    // Tests for addStudentToCourse

    @Test
    @DisplayName("addStudentToCourse | Should add student successfully")
    void testAddStudentToCourse_Success() {

        when(courseRepository.findById(99L)).thenReturn(Optional.of(existingCourse));
        when(userRepository.findById(5L)).thenReturn(Optional.of(existingStudent));

        CourseDTO result = courseService.addStudentToCourse(99L, 5L);

        assertNotNull(result);
        assertTrue(existingCourse.getStudents().contains(existingStudent));
        
        verify(courseRepository, times(1)).findById(99L);
        verify(userRepository, times(1)).findById(5L);
        verify(userRepository, times(1)).save(existingStudent);
    }

    @Test
    @DisplayName("addStudentToCourse | Should throw ResourceNotFoundException when Course not found")
    void testAddStudentToCourse_Failure_CourseNotFound() {

        when(courseRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.addStudentToCourse(99L, 5L);
        });
        
        verify(userRepository, never()).findById(anyLong());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("addStudentToCourse | Should throw ResourceNotFoundException when Student not found")
    void testAddStudentToCourse_Failure_StudentNotFound() {

        when(courseRepository.findById(99L)).thenReturn(Optional.of(existingCourse));
        when(userRepository.findById(5L)).thenReturn(Optional.empty()); // Student não encontrado

        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.addStudentToCourse(99L, 5L);
        });
        
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("addStudentToCourse | Should throw ResourceNotFoundException when User is not a Student")
    void testAddStudentToCourse_Failure_UserIsNotAStudent() {

        when(courseRepository.findById(99L)).thenReturn(Optional.of(existingCourse));
        when(userRepository.findById(4L)).thenReturn(Optional.of(adminUser)); // Encontra um Admin

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> courseService.addStudentToCourse(99L, 4L)
        );
        assertEquals("Student not found with id: 4", exception.getMessage());
        
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("addStudentToCourse | Should throw DuplicateResourceException when Student already enrolled")
    void testAddStudentToCourse_Failure_AlreadyAssigned() {

        existingCourse.getStudents().add(existingStudent);
        
        when(courseRepository.findById(99L)).thenReturn(Optional.of(existingCourse));
        when(userRepository.findById(5L)).thenReturn(Optional.of(existingStudent));

        assertThrows(DuplicateResourceException.class, () -> {
            courseService.addStudentToCourse(99L, 5L);
        });

        verify(userRepository, never()).save(any());
    }
}