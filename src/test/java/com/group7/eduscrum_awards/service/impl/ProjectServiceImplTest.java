package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.repository.CourseRepository;
import com.group7.eduscrum_awards.repository.ProjectRepository;
import com.group7.eduscrum_awards.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ProjectServiceImpl.
 * These tests isolate the service layer logic and use
 * Mockito to simulate the behavior of its repositories.
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock 
    private UserRepository userRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;
    
    private Course existingCourse;
    private ProjectCreateDTO createDTO;
    private Project savedProject;
    private Student student;

    @BeforeEach
    void setUp() {
        // Setup base data
        existingCourse = new Course("Test Course");
        existingCourse.setId(1L);

        createDTO = new ProjectCreateDTO();
        createDTO.setName("Test Project");
        createDTO.setDescription("Desc");
        createDTO.setStartDate(LocalDate.now());
        createDTO.setEndDate(LocalDate.now().plusMonths(1));

        savedProject = new Project(createDTO.getName(), createDTO.getDescription(), existingCourse, createDTO.getStartDate(), createDTO.getEndDate());
        savedProject.setId(10L);

        student = new Student("John", "john@test.com", "pass");
        student.setId(5L);
    }

    // Tests for createProject

    @Test
    @DisplayName("createProject | Should create project successfully")
    void testCreateProject_Success() {

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        when(projectRepository.findByNameAndCourse("Test Project", existingCourse)).thenReturn(Optional.empty());
        doReturn(savedProject).when(projectRepository).save(notNull());

        ProjectDTO result = projectService.createProject(1L, createDTO);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Test Project", result.getName());
        
        // CORREÇÃO: Validar com os dados definidos no setUp() ("Desc")
        assertEquals("Desc", result.getDescription());
        
        // CORREÇÃO: Validar com a data definida no setUp() (LocalDate.now())
        assertEquals(createDTO.getStartDate(), result.getStartDate());
        assertEquals(1L, result.getCourseId());

        verify(courseRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).findByNameAndCourse("Test Project", existingCourse);
        verify(projectRepository, times(1)).save(notNull());
    }

    @Test
    @DisplayName("createProject | Should throw ResourceNotFoundException when Course not found")
    void testCreateProject_Failure_CourseNotFound() {

        when(courseRepository.findById(1L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> projectService.createProject(1L, createDTO)
        );
        
        assertEquals("Course not found with id: 1", exception.getMessage());

        verify(courseRepository, times(1)).findById(1L);
        verify(projectRepository, never()).findByNameAndCourse(anyString(), any());
        verify(projectRepository, never()).save(any());
    }

    @Test
    @DisplayName("createProject | Should throw DuplicateResourceException when Project name exists in Course")
    void testCreateProject_Failure_DuplicateName() {

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        when(projectRepository.findByNameAndCourse("Test Project", existingCourse)).thenReturn(Optional.of(savedProject));

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> projectService.createProject(1L, createDTO)
        );
        
        assertEquals("A Project with the name 'Test Project' already exists in this course.", exception.getMessage());
        
        verify(courseRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).findByNameAndCourse("Test Project", existingCourse);
        verify(projectRepository, never()).save(any());
    }

    // Tests for getMyProjects

    @Test
    @DisplayName("getMyProjects | Should throw exception if student not found")
    void testGetMyProjects_StudentNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> projectService.getMyProjects(99L));
        
        verify(projectRepository, never()).findProjectsByStudentId(any());
    }
}