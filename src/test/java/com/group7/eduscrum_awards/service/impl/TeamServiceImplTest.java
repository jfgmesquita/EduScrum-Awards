package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.dto.TeamCreateDTO;
import com.group7.eduscrum_awards.dto.TeamDTO;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.User;
import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Team;
import com.group7.eduscrum_awards.repository.ProjectRepository;
import com.group7.eduscrum_awards.repository.TeamRepository;
import com.group7.eduscrum_awards.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the TeamServiceImpl.
 * These tests isolate the service layer logic and use
 * Mockito to simulate the behavior of its repositories.
 */
@ExtendWith(MockitoExtension.class)
class TeamServiceImplTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private ProjectRepository projectRepository;
    
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TeamServiceImpl teamService;

    private Project existingProject;
    private TeamCreateDTO createDTO;
    private Team savedTeam;
    private Student existingStudent;
    private User adminUser;

    @BeforeEach
    void setUp() {
        // Common test data setup
        existingProject = new Project("Test Project", "Desc", null, null, null);
        existingProject.setId(1L);
        
        // Simulate the DTO from the controller
        createDTO = new TeamCreateDTO();
        createDTO.setName("Test Team");
        createDTO.setGroupNumber(1);

        // Simulate the saved Team entity
        savedTeam = new Team(
            createDTO.getName(),
            createDTO.getGroupNumber(),
            existingProject
        );
        savedTeam.setId(10L);

        // Data for addStudentToTeam tests
        existingStudent = new Student("Test Student", "student@test.com", "pass");
        existingStudent.setId(4L);

        adminUser = new User("Test Admin", "admin@test.com", "pass", Role.ADMIN);
        adminUser.setId(5L);
    }

    // Tests for createTeam method

    @Test
    @DisplayName("createTeam | Should create team successfully")
    void testCreateTeam_Success() {

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(teamRepository.findByGroupNumberAndProject(1, existingProject)).thenReturn(Optional.empty());
        when(teamRepository.findByNameAndProject("Test Team", existingProject)).thenReturn(Optional.empty());
        doReturn(savedTeam).when(teamRepository).save(notNull());

        TeamDTO result = teamService.createTeam(1L, createDTO);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Test Team", result.getName());
        assertEquals(1, result.getGroupNumber());
        assertEquals(1L, result.getProjectId());
        
        verify(projectRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findByGroupNumberAndProject(1, existingProject);
        verify(teamRepository, times(1)).findByNameAndProject("Test Team", existingProject);
        verify(teamRepository, times(1)).save(notNull());
    }

    @Test
    @DisplayName("createTeam | Should throw ResourceNotFoundException when Project not found")
    void testCreateTeam_Failure_ProjectNotFound() {

        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> teamService.createTeam(1L, createDTO)
        );
        
        assertEquals("Project not found with id: 1", exception.getMessage());
        
        verify(projectRepository, times(1)).findById(1L);
        verify(teamRepository, never()).findByGroupNumberAndProject(anyInt(), any());
        verify(teamRepository, never()).findByNameAndProject(anyString(), any());
        verify(teamRepository, never()).save(any());
    }

    @Test
    @DisplayName("createTeam | Should throw DuplicateResourceException when Group Number exists in Project")
    void testCreateTeam_Failure_DuplicateGroupNumber() {

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(teamRepository.findByGroupNumberAndProject(1, existingProject)).thenReturn(Optional.of(savedTeam));

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> teamService.createTeam(1L, createDTO)
        );
        
        assertEquals("A team with group number 1 already exists in this project.", exception.getMessage());
        
        verify(projectRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findByGroupNumberAndProject(1, existingProject);
        verify(teamRepository, never()).findByNameAndProject(anyString(), any());
        verify(teamRepository, never()).save(any());
    }

    @Test
    @DisplayName("createTeam | Should throw DuplicateResourceException when Name exists in Project")
    void testCreateTeam_Failure_DuplicateName() {
        
        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(teamRepository.findByGroupNumberAndProject(1, existingProject)).thenReturn(Optional.empty());
        when(teamRepository.findByNameAndProject("Test Team", existingProject)).thenReturn(Optional.of(savedTeam));

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> teamService.createTeam(1L, createDTO)
        );
        
        assertEquals("A team with the name 'Test Team' already exists in this project.", exception.getMessage());
        
        verify(projectRepository, times(1)).findById(1L);
        verify(teamRepository, times(1)).findByGroupNumberAndProject(1, existingProject);
        verify(teamRepository, times(1)).findByNameAndProject("Test Team", existingProject);
        verify(teamRepository, never()).save(any());
    }

    // Test for addStudentToTeam

    @Test
    @DisplayName("addStudentToTeam | Should add student successfully")
    void testAddStudentToTeam_Success() {
        when(teamRepository.findById(10L)).thenReturn(Optional.of(savedTeam));
        when(userRepository.findById(4L)).thenReturn(Optional.of(existingStudent));

        TeamDTO result = teamService.addStudentToTeam(10L, 4L);

        assertNotNull(result);
        assertTrue(savedTeam.getMembers().contains(existingStudent));
        
        verify(teamRepository, times(1)).findById(10L);
        verify(userRepository, times(1)).findById(4L);
        verify(teamRepository, times(1)).save(savedTeam);
    }

    @Test
    @DisplayName("addStudentToTeam | Should throw ResourceNotFoundException when Team not found")
    void testAddStudentToTeam_Failure_TeamNotFound() {
        when(teamRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            teamService.addStudentToTeam(10L, 4L);
        });

        verify(userRepository, never()).findById(anyLong());
        verify(teamRepository, never()).save(any());
    }

    @Test
    @DisplayName("addStudentToTeam | Should throw ResourceNotFoundException when Student not found")
    void testAddStudentToTeam_Failure_StudentNotFound() {
        when(teamRepository.findById(10L)).thenReturn(Optional.of(savedTeam));
        when(userRepository.findById(4L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            teamService.addStudentToTeam(10L, 4L);
        });

        verify(teamRepository, never()).save(any());
    }

    @Test
    @DisplayName("addStudentToTeam | Should throw ResourceNotFoundException when User is not a Student")
    void testAddStudentToTeam_Failure_UserIsNotStudent() {
        when(teamRepository.findById(10L)).thenReturn(Optional.of(savedTeam));
        when(userRepository.findById(5L)).thenReturn(Optional.of(adminUser)); // Find an Admin

        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> teamService.addStudentToTeam(10L, 5L)
        );

        assertEquals("Student not found with id: 5", exception.getMessage());
        verify(teamRepository, never()).save(any());
    }

    @Test
    @DisplayName("addStudentToTeam | Should throw DuplicateResourceException when Student already in Team")
    void testAddStudentToTeam_Failure_AlreadyInTeam() {

        savedTeam.getMembers().add(existingStudent);
        when(teamRepository.findById(10L)).thenReturn(Optional.of(savedTeam));
        when(userRepository.findById(4L)).thenReturn(Optional.of(existingStudent));

        assertThrows(DuplicateResourceException.class, () -> {
            teamService.addStudentToTeam(10L, 4L);
        });

        verify(teamRepository, never()).save(any());
    }
}