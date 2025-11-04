package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.CourseCreateDTO;
import com.group7.eduscrum_awards.dto.CourseDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Degree;
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
import static org.mockito.ArgumentMatchers.anyString;
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
        assertEquals("Test Course", result.getName());

        verify(degreeRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).findByNameAndDegree("Test Course", existingDegree);
        verify(courseRepository, times(1)).save(notNull());
    }

    @Test
    @DisplayName("registerCourse | Should throw ResourceNotFoundException when Degree not found")
    void testRegisterCourseForDegree_Failure_DegreeNotFound() {

        when(degreeRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> {
                courseService.registerCourseForDegree(1L, createDTO);
            }
        );
        assertEquals("Degree not found with id: 1", exception.getMessage());
        
        verify(degreeRepository, times(1)).findById(1L);
        verify(courseRepository, never()).findByNameAndDegree(anyString(), any());
        verify(courseRepository, never()).save(any());
    }

    @Test
    @DisplayName("registerCourse | Should throw DuplicateResourceException when name exists in Degree")
    void testRegisterCourseForDegree_Failure_DuplicateName() {

        when(degreeRepository.findById(1L)).thenReturn(Optional.of(existingDegree));
        when(courseRepository.findByNameAndDegree("Test Course", existingDegree)).thenReturn(Optional.of(existingCourse)); // Use existingCourse here

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> {
                courseService.registerCourseForDegree(1L, createDTO);
            }
        );
        assertEquals("A Course with the name 'Test Course' already exists for this Degree.", exception.getMessage());
        
        verify(degreeRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).findByNameAndDegree("Test Course", existingDegree);
        verify(courseRepository, never()).save(any());
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
        
        verify(courseRepository, times(1)).findById(99L);
        verify(userRepository, times(1)).findById(3L);
        verify(courseRepository, times(1)).save(existingCourse);
    }

    @Test
    @DisplayName("addTeacherToCourse | Should throw ResourceNotFoundException when Course not found")
    void testAddTeacherToCourse_Failure_CourseNotFound() {

        when(courseRepository.findById(99L)).thenReturn(Optional.empty());
        
        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.addTeacherToCourse(99L, 3L);
        });
        
        verify(userRepository, never()).findById(anyLong()); // Never looks for a user
        verify(courseRepository, never()).save(any());
    }

    @Test
    @DisplayName("addTeacherToCourse | Should throw ResourceNotFoundException when Teacher not found")
    void testAddTeacherToCourse_Failure_TeacherNotFound() {

        when(courseRepository.findById(99L)).thenReturn(Optional.of(existingCourse));
        when(userRepository.findById(3L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            courseService.addTeacherToCourse(99L, 3L);
        });
        
        verify(courseRepository, never()).save(any());
    }

    @Test
    @DisplayName("addTeacherToCourse | Should throw ResourceNotFoundException when User is not a Teacher")
    void testAddTeacherToCourse_Failure_UserIsNotATeacher() {

        when(courseRepository.findById(99L)).thenReturn(Optional.of(existingCourse));
        when(userRepository.findById(4L)).thenReturn(Optional.of(adminUser));

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> {
                courseService.addTeacherToCourse(99L, 4L);
            }
        );
        assertEquals("Teacher not found with id: 4", exception.getMessage());
        
        verify(courseRepository, never()).save(any());
    }

    @Test
    @DisplayName("addTeacherToCourse | Should throw DuplicateResourceException when Teacher already assigned")
    void testAddTeacherToCourse_Failure_AlreadyAssigned() {

        existingCourse.getTeachers().add(existingTeacher);
        when(courseRepository.findById(99L)).thenReturn(Optional.of(existingCourse));
        when(userRepository.findById(3L)).thenReturn(Optional.of(existingTeacher));

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> {
                courseService.addTeacherToCourse(99L, 3L);
            }
        );
        assertEquals("Teacher with id 3 is already assigned to this course.", exception.getMessage());
        
        verify(courseRepository, times(1)).findById(99L);
        verify(userRepository, times(1)).findById(3L);
        verify(courseRepository, never()).save(any());
    }
}