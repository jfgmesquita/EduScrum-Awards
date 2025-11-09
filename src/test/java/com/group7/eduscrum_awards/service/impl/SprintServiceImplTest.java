package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.SprintCreateDTO;
import com.group7.eduscrum_awards.dto.SprintDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.*;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.model.enums.TeamRole;
import com.group7.eduscrum_awards.repository.ProjectRepository;
import com.group7.eduscrum_awards.repository.SprintRepository;
import com.group7.eduscrum_awards.repository.TeamMemberRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the SprintServiceImpl.
 * Mocks all repositories and simulates the security context
 * to test business and authorization logic.
 */
@ExtendWith(MockitoExtension.class)
class SprintServiceImplTest {

    @Mock
    private SprintRepository sprintRepository;
    @Mock
    private ProjectRepository projectRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private SprintServiceImpl sprintService;

    // --- Test Data ---
    private Project existingProject;
    private Student poStudent; 
    private Student devStudent;
    private TeamMember poMembership;
    private SprintCreateDTO createDTO;
    private Sprint savedSprint;

    @BeforeEach
    void setUp() {
        // Common test data setup
        existingProject = new Project("Test Project", "Desc", null, null, null);
        existingProject.setId(1L);

        // Product Owner student
        poStudent = new Student("PO User", "po@test.com", "pass");
        poStudent.setId(4L);
        poStudent.setRole(Role.STUDENT);

        // Another student who is not the PO
        devStudent = new Student("Dev User", "dev@test.com", "pass");
        devStudent.setId(5L);
        devStudent.setRole(Role.STUDENT);

        // Product Owner membership
        poMembership = new TeamMember();
        poMembership.setStudent(poStudent);
        poMembership.setProject(existingProject);
        poMembership.setTeamRole(TeamRole.PRODUCT_OWNER);

        // Sprint creation DTO
        createDTO = new SprintCreateDTO();
        createDTO.setSprintNumber(1);
        createDTO.setFinalGoal("Test Goal");
        createDTO.setStartDate(LocalDate.now());
        createDTO.setEndDate(LocalDate.now().plusDays(14));

        // Saved Sprint mock
        savedSprint = new Sprint(1, "Test Goal", LocalDate.now(), LocalDate.now().plusDays(14), existingProject);
        savedSprint.setId(10L);
    }

    /**
     * Helper method to simulate a user being logged in.
     * @param user The User (Student) to set as the principal.
     */
    private void mockSecurityContext(User user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /** Cleans up the SecurityContext after each test. */
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("createSprint | Should create sprint successfully for Product Owner")
    void testCreateSprint_Success() {
        mockSecurityContext(poStudent);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(teamMemberRepository.findByStudentAndProject(poStudent, existingProject)).thenReturn(Optional.of(poMembership));
        when(sprintRepository.findBySprintNumberAndProject(1, existingProject)).thenReturn(Optional.empty());
        doReturn(savedSprint).when(sprintRepository).save(any(Sprint.class));

        SprintDTO result = sprintService.createSprint(1L, createDTO);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals(1, result.getSprintNumber());
        
        verify(projectRepository).findById(1L);
        verify(teamMemberRepository).findByStudentAndProject(poStudent, existingProject);
        verify(sprintRepository).findBySprintNumberAndProject(1, existingProject);
        verify(sprintRepository).save(any(Sprint.class));
    }

    @Test
    @DisplayName("createSprint | Should throw ResourceNotFoundException when Project not found")
    void testCreateSprint_Failure_ProjectNotFound() {
        mockSecurityContext(poStudent);
        
        when(projectRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            sprintService.createSprint(1L, createDTO);
        });
        
        verify(projectRepository).findById(1L);
        verify(teamMemberRepository, never()).findByStudentAndProject(any(), any());
    }

    @Test
    @DisplayName("createSprint | Should throw AccessDeniedException when user is not Product Owner")
    void testCreateSprint_Failure_NotProductOwner() {
        mockSecurityContext(devStudent);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(teamMemberRepository.findByStudentAndProject(devStudent, existingProject)).thenReturn(Optional.empty()); // Not the PO

        assertThrows(AccessDeniedException.class, () -> {
            sprintService.createSprint(1L, createDTO);
        });
        
        verify(projectRepository).findById(1L);
        verify(teamMemberRepository).findByStudentAndProject(devStudent, existingProject);
        verify(sprintRepository, never()).save(any());
    }

    @Test
    @DisplayName("createSprint | Should throw DuplicateResourceException when Sprint Number exists")
    void testCreateSprint_Failure_DuplicateSprintNumber() {
        mockSecurityContext(poStudent);

        when(projectRepository.findById(1L)).thenReturn(Optional.of(existingProject));
        when(teamMemberRepository.findByStudentAndProject(poStudent, existingProject)).thenReturn(Optional.of(poMembership));
        when(sprintRepository.findBySprintNumberAndProject(1, existingProject)).thenReturn(Optional.of(savedSprint)); // Duplicate found

        assertThrows(DuplicateResourceException.class, () -> {
            sprintService.createSprint(1L, createDTO);
        });

        verify(projectRepository).findById(1L);
        verify(teamMemberRepository).findByStudentAndProject(poStudent, existingProject);
        verify(sprintRepository).findBySprintNumberAndProject(1, existingProject);
        verify(sprintRepository, never()).save(any());
    }
}