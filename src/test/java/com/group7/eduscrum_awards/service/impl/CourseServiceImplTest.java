package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.CourseCreateDTO;
import com.group7.eduscrum_awards.dto.CourseDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.repository.CourseRepository;
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

    @InjectMocks
    private CourseServiceImpl courseService;

    private CourseCreateDTO createDTO;
    private Course existingCourse;

    /** Sets up common test data before each test. */
    @BeforeEach
    void setUp() {
        // Simulates the DTO from the controller
        createDTO = new CourseCreateDTO();
        createDTO.setName("Test Course");

        // Simulates a record already in the database
        existingCourse = new Course("Test Course");
        existingCourse.setId(1L);
    }

    /**
     * Test Scenario 1: Successful registration of a new course.
     */
    @Test
    @DisplayName("Should register course successfully when name is unique")
    void testRegisterCourse_Success() {

        when(courseRepository.findByName("Test Course")).thenReturn(Optional.empty());
        doReturn(existingCourse).when(courseRepository).save(notNull());

        CourseDTO result = courseService.registerCourse(createDTO);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Course", result.getName());

        verify(courseRepository, times(1)).findByName("Test Course");
        verify(courseRepository, times(1)).save(notNull());
    }

    /**
     * Test Scenario 2: Attempting to register a duplicate course.
     */
    @Test
    @DisplayName("Should throw DuplicateResourceException when name already exists")
    void testRegisterCourse_Failure_DuplicateName() {

        when(courseRepository.findByName("Test Course")).thenReturn(Optional.of(existingCourse));

        DuplicateResourceException exceptionThrown = assertThrows(
            DuplicateResourceException.class,
            () -> {
                courseService.registerCourse(createDTO); // This should throw
            }
        );
        assertEquals("A Course with the name 'Test Course' already exists.", exceptionThrown.getMessage());

        verify(courseRepository, times(1)).findByName("Test Course");
        verify(courseRepository, never()).save(any(Course.class));
    }
}