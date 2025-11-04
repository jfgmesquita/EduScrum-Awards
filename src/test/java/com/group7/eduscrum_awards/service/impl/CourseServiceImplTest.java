package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.CourseCreateDTO;
import com.group7.eduscrum_awards.dto.CourseDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Degree;
import com.group7.eduscrum_awards.repository.CourseRepository;
import com.group7.eduscrum_awards.repository.DegreeRepository;
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

@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {

    @Mock
    private CourseRepository courseRepository;
    
    @Mock
    private DegreeRepository degreeRepository;

    @InjectMocks
    private CourseServiceImpl courseService;

    private Degree existingDegree;
    private CourseCreateDTO createDTO;
    private Course savedCourse;

    @BeforeEach
    void setUp() {
        // Simulates the parent Degree
        existingDegree = new Degree("Test Degree");
        existingDegree.setId(1L);

        // Simulates the DTO from the controller
        createDTO = new CourseCreateDTO();
        createDTO.setName("Test Course");

        // Simulates the saved Course entity
        savedCourse = new Course("Test Course");
        savedCourse.setId(99L);
        savedCourse.setDegree(existingDegree);
    }

    /**
     * Test Scenario 1: Successful registration of a new course for a degree.
     */
    @Test
    @DisplayName("Should register course successfully for a valid Degree")
    void testRegisterCourseForDegree_Success() {
  
        when(degreeRepository.findById(1L)).thenReturn(Optional.of(existingDegree));
        when(courseRepository.findByNameAndDegree("Test Course", existingDegree)).thenReturn(Optional.empty());
        doReturn(savedCourse).when(courseRepository).save(notNull());

        CourseDTO result = courseService.registerCourseForDegree(1L, createDTO);

        assertNotNull(result);
        assertEquals(99L, result.getId());
        assertEquals("Test Course", result.getName());

        verify(degreeRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).findByNameAndDegree("Test Course", existingDegree);
        verify(courseRepository, times(1)).save(notNull());
    }

    /**
     * Test Scenario 2: Failure when Degree ID does not exist.
     */
    @Test
    @DisplayName("Should throw ResourceNotFoundException when Degree does not exist")
    void testRegisterCourseForDegree_Failure_DegreeNotFound() {

        when(degreeRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> {
                courseService.registerCourseForDegree(1L, createDTO); // This should throw
            }
        );
        assertEquals("Degree not found with id: 1", exception.getMessage());
        
        verify(degreeRepository, times(1)).findById(1L);
        verify(courseRepository, never()).findByNameAndDegree(anyString(), any());
        verify(courseRepository, never()).save(any());
    }

    /**
     * Test Scenario 3: Failure when Course name is duplicate within that Degree.
     */
    @Test
    @DisplayName("Should throw DuplicateResourceException when name already exists in Degree")
    void testRegisterCourseForDegree_Failure_DuplicateName() {

        when(degreeRepository.findById(1L)).thenReturn(Optional.of(existingDegree));
        when(courseRepository.findByNameAndDegree("Test Course", existingDegree)).thenReturn(Optional.of(savedCourse));

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> {
                courseService.registerCourseForDegree(1L, createDTO); // This should throw
            }
        );
        assertEquals("A Course with the name 'Test Course' already exists for this Degree.", exception.getMessage());
        
        verify(degreeRepository, times(1)).findById(1L);
        verify(courseRepository, times(1)).findByNameAndDegree("Test Course", existingDegree);
        verify(courseRepository, never()).save(any());
    }
}