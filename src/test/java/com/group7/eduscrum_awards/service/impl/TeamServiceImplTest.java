package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.TeamCreateDTO;
import com.group7.eduscrum_awards.dto.TeamDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Team;
import com.group7.eduscrum_awards.repository.ProjectRepository;
import com.group7.eduscrum_awards.repository.TeamRepository;
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
import static org.mockito.ArgumentMatchers.any;
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

    @InjectMocks
    private TeamServiceImpl teamService;

    private Project existingProject;
    private TeamCreateDTO createDTO;
    private Team savedTeam;

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
    }

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
}