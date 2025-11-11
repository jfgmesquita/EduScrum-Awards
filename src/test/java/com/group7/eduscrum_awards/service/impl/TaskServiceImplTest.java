package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.TaskCreateDTO;
import com.group7.eduscrum_awards.dto.TaskDTO;
import com.group7.eduscrum_awards.dto.TaskAssignDTO;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.*;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.model.enums.TaskStatus;
import com.group7.eduscrum_awards.model.enums.TeamRole;
import com.group7.eduscrum_awards.repository.SprintRepository;
import com.group7.eduscrum_awards.repository.TaskRepository;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the TaskServiceImpl.
 * Mocks all repositories and simulates the security context
 * to test business and authorization logic.
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;
    @Mock
    private SprintRepository sprintRepository;
    @Mock
    private TeamMemberRepository teamMemberRepository;

    @InjectMocks
    private TaskServiceImpl taskService;

    // Test Data
    private Project existingProject;
    private Sprint existingSprint;
    private Student poStudent;
    private Student devStudent;
    private TeamMember poMembership;
    private TeamMember devMembership;
    private TaskCreateDTO createDTO;
    private TaskAssignDTO assignDTO;
    private Task savedTask;

    @BeforeEach
    void setUp() {
        // Initialize test data
        existingProject = new Project("Test Project", "Desc", null, null, null);
        existingProject.setId(1L);

        existingSprint = new Sprint(1, "Test Sprint", LocalDate.now(), LocalDate.now().plusDays(7), existingProject);
        existingSprint.setId(10L);

        // Users
        poStudent = new Student("PO User", "po@test.com", "pass");
        poStudent.setId(4L);
        poStudent.setRole(Role.STUDENT);

        devStudent = new Student("Dev User", "dev@test.com", "pass");
        devStudent.setId(5L);
        devStudent.setRole(Role.STUDENT);

        // Team Memberships
        poMembership = new TeamMember();
        poMembership.setStudent(poStudent);
        poMembership.setProject(existingProject);
        poMembership.setTeamRole(TeamRole.PRODUCT_OWNER);

        // Developer Membership
        devMembership = new TeamMember();
        devMembership.setId(2L);
        devMembership.setStudent(devStudent);
        devMembership.setProject(existingProject);
        devMembership.setTeamRole(TeamRole.DEVELOPER);

        // Task Creation DTO
        createDTO = new TaskCreateDTO();
        createDTO.setDescription("New test task");

        // Task Assignment DTO
        assignDTO = new TaskAssignDTO();
        assignDTO.setTeamMemberId(2L);

        // Saved Task Mock
        savedTask = new Task("New test task", existingSprint);
        savedTask.setId(100L);
    }

    /** Helper method to simulate a user being logged in. */
    private void mockSecurityContext(User user) {
        Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    /** Cleans up the SecurityContext after each test. */
    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // Tests for createTask

    @Test
    @DisplayName("createTask | Should create task successfully for Product Owner")
    void testCreateTask_Success() {
        mockSecurityContext(poStudent);

        when(sprintRepository.findById(10L)).thenReturn(Optional.of(existingSprint));
        when(teamMemberRepository.findByStudentAndProject(poStudent, existingProject)).thenReturn(Optional.of(poMembership));
        doReturn(savedTask).when(taskRepository).save(any(Task.class));

        TaskDTO result = taskService.createTask(10L, createDTO);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals("New test task", result.getDescription());
        assertEquals(TaskStatus.TODO, result.getStatus());
        assertEquals(10L, result.getSprintId());
        assertNull(result.getTeamMemberId());
        
        verify(sprintRepository).findById(10L);
        verify(teamMemberRepository).findByStudentAndProject(poStudent, existingProject);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("createTask | Should throw ResourceNotFoundException when Sprint not found")
    void testCreateTask_Failure_SprintNotFound() {
        mockSecurityContext(poStudent);
        
        when(sprintRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.createTask(10L, createDTO);
        });
        
        verify(sprintRepository).findById(10L);
        verify(teamMemberRepository, never()).findByStudentAndProject(any(), any());
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("createTask | Should throw AccessDeniedException when user is not Product Owner")
    void testCreateTask_Failure_NotProductOwner() {
        mockSecurityContext(devStudent); // Log in as a Developer

        when(sprintRepository.findById(10L)).thenReturn(Optional.of(existingSprint));
        when(teamMemberRepository.findByStudentAndProject(devStudent, existingProject)).thenReturn(Optional.empty()); // Not the PO

        assertThrows(AccessDeniedException.class, () -> {
            taskService.createTask(10L, createDTO);
        });
        
        verify(sprintRepository).findById(10L);
        verify(teamMemberRepository).findByStudentAndProject(devStudent, existingProject);
        verify(taskRepository, never()).save(any());
    }

    // Tests for assignTask

    @Test
    @DisplayName("assignTask | Should assign task successfully for Product Owner")
    void testAssignTask_Success() {
        mockSecurityContext(poStudent);

        when(taskRepository.findById(100L)).thenReturn(Optional.of(savedTask));
        when(teamMemberRepository.findById(2L)).thenReturn(Optional.of(devMembership));
        when(teamMemberRepository.findByStudentAndProject(poStudent, existingProject)).thenReturn(Optional.of(poMembership));
        doReturn(savedTask).when(taskRepository).save(any(Task.class));

        TaskDTO result = taskService.assignTask(100L, assignDTO);

        assertNotNull(result);
        assertEquals(100L, result.getId());
        assertEquals(2L, result.getTeamMemberId()); // Check assignment
        
        verify(taskRepository).findById(100L);
        verify(teamMemberRepository).findById(2L);
        verify(teamMemberRepository).findByStudentAndProject(poStudent, existingProject);
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("assignTask | Should throw ResourceNotFoundException when Task not found")
    void testAssignTask_Failure_TaskNotFound() {
        mockSecurityContext(poStudent);
        
        when(taskRepository.findById(100L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.assignTask(100L, assignDTO);
        });

        verify(teamMemberRepository, never()).findById(anyLong());
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("assignTask | Should throw ResourceNotFoundException when Developer not found")
    void testAssignTask_Failure_DeveloperNotFound() {
        mockSecurityContext(poStudent);

        when(taskRepository.findById(100L)).thenReturn(Optional.of(savedTask));
        when(teamMemberRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            taskService.assignTask(100L, assignDTO);
        });

        verify(taskRepository).findById(100L);
        verify(teamMemberRepository).findById(2L);
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("assignTask | Should throw AccessDeniedException when user is not Product Owner")
    void testAssignTask_Failure_NotProductOwner() {
        mockSecurityContext(devStudent); // Log in as Developer

        when(taskRepository.findById(100L)).thenReturn(Optional.of(savedTask));
        when(teamMemberRepository.findById(2L)).thenReturn(Optional.of(devMembership));
        when(teamMemberRepository.findByStudentAndProject(devStudent, existingProject)).thenReturn(Optional.empty()); // Not the PO

        assertThrows(AccessDeniedException.class, () -> {
            taskService.assignTask(100L, assignDTO);
        });
        
        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("assignTask | Should throw IllegalArgumentException when Developer is not a DEVELOPER")
    void testAssignTask_Failure_MemberNotDeveloper() {
        mockSecurityContext(poStudent);
        poMembership.setTeamRole(TeamRole.PRODUCT_OWNER); // Change target to be a PO

        when(taskRepository.findById(100L)).thenReturn(Optional.of(savedTask));
        when(teamMemberRepository.findById(1L)).thenReturn(Optional.of(poMembership)); // Try to assign to the PO
        when(teamMemberRepository.findByStudentAndProject(poStudent, existingProject)).thenReturn(Optional.of(poMembership));

        assignDTO.setTeamMemberId(1L); // Set DTO to assign to PO
        
        assertThrows(IllegalArgumentException.class, () -> {
            taskService.assignTask(100L, assignDTO);
        });

        verify(taskRepository, never()).save(any());
    }

    @Test
    @DisplayName("assignTask | Should throw IllegalArgumentException when Developer is on different project")
    void testAssignTask_Failure_DeveloperWrongProject() {
        mockSecurityContext(poStudent);
        
        Project otherProject = new Project();
        otherProject.setId(2L);
        devMembership.setProject(otherProject); // Put the dev on a different project

        when(taskRepository.findById(100L)).thenReturn(Optional.of(savedTask));
        when(teamMemberRepository.findById(2L)).thenReturn(Optional.of(devMembership));
        when(teamMemberRepository.findByStudentAndProject(poStudent, existingProject)).thenReturn(Optional.of(poMembership));

        assertThrows(IllegalArgumentException.class, () -> {
            taskService.assignTask(100L, assignDTO);
        });

        verify(taskRepository, never()).save(any());
    }
}