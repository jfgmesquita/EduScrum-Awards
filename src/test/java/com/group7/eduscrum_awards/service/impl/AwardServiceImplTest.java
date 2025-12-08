package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.AwardAssignmentRequestDTO;
import com.group7.eduscrum_awards.dto.AwardCreateDTO;
import com.group7.eduscrum_awards.dto.AwardDTO;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.*;
import com.group7.eduscrum_awards.model.enums.AwardScope;
import com.group7.eduscrum_awards.model.enums.AwardType;
import com.group7.eduscrum_awards.model.enums.TeamRole;
import com.group7.eduscrum_awards.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the AwardServiceImpl.
 * Validates logic for creating awards, assigning awards (Student vs Team), and listing available awards.
 */
@ExtendWith(MockitoExtension.class)
class AwardServiceImplTest {

    @Mock private AwardRepository awardRepository;
    @Mock private AwardAssignmentRepository assignmentRepository;
    @Mock private CourseRepository courseRepository;
    @Mock private ProjectRepository projectRepository;
    @Mock private UserRepository userRepository;
    @Mock private TeamRepository teamRepository;

    @InjectMocks
    private AwardServiceImpl awardService;

    // Test Data
    private Course course;
    private Project project;
    private Student student;
    private Team team;
    private Award globalAward;
    private Award customAward;
    private AwardCreateDTO createDTO;
    private AwardAssignmentRequestDTO assignmentRequest;

    @BeforeEach
    void setUp() {
        // Setup Course & Project
        course = new Course("Software Quality");
        course.setId(1L);

        project = new Project("Final Project", "Desc", course, null, null);
        project.setId(10L);

        // Setup Student
        student = new Student("Alice", "alice@test.com", "pass");
        student.setId(100L);

        // Setup Team with Members
        team = new Team("Alpha Team", 1, project);
        team.setId(50L);
        
        // Add student to team to simulate membership
        TeamMember member = new TeamMember();
        member.setStudent(student);
        member.setTeam(team);
        member.setProject(project);
        member.setTeamRole(TeamRole.DEVELOPER);
        
        Set<TeamMember> members = new HashSet<>();
        members.add(member);
        team.addStudent(student, TeamRole.DEVELOPER);

        // Setup Awards
        globalAward = new Award("Global Badge", "Desc", 10, AwardType.AUTOMATIC, AwardScope.STUDENT, "icon", null);
        globalAward.setId(1L);

        customAward = new Award("Custom Badge", "Desc", 5, AwardType.MANUAL, AwardScope.STUDENT, null, course);
        customAward.setId(2L);

        // Setup DTOs
        createDTO = new AwardCreateDTO();
        createDTO.setName("New Award");
        createDTO.setDescription("Description");
        createDTO.setPoints(5);

        assignmentRequest = new AwardAssignmentRequestDTO();
        assignmentRequest.setProjectId(10L);
    }

    // Tests for createCustomAward

    @Test
    @DisplayName("createCustomAward | Should create award successfully")
    void testCreateCustomAward_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(awardRepository.save(any(Award.class))).thenAnswer(i -> {
            Award a = i.getArgument(0);
            a.setId(99L);
            return a;
        });

        AwardDTO result = awardService.createCustomAward(1L, createDTO);

        assertNotNull(result);
        assertEquals(99L, result.getId());
        assertEquals("New Award", result.getName());
        assertEquals(AwardType.MANUAL, result.getType());
        
        verify(courseRepository).findById(1L);
        verify(awardRepository).save(any(Award.class));
    }

    @Test
    @DisplayName("createCustomAward | Should throw exception if Course not found")
    void testCreateCustomAward_CourseNotFound() {
        when(courseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            awardService.createCustomAward(1L, createDTO)
        );
        verify(awardRepository, never()).save(any());
    }

    // Tests for assignAward (Individual)

    @Test
    @DisplayName("assignAward | Should assign to Student successfully")
    void testAssignAward_Student_Success() {
        assignmentRequest.setStudentId(100L); // Set target student

        when(awardRepository.findById(2L)).thenReturn(Optional.of(customAward));
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(userRepository.findById(100L)).thenReturn(Optional.of(student));

        awardService.assignAward(2L, assignmentRequest);

        // Verify that ONE assignment was saved
        verify(assignmentRepository, times(1)).save(any(AwardAssignment.class));
    }

    @Test
    @DisplayName("assignAward | Should throw exception if Student not found")
    void testAssignAward_Student_NotFound() {
        assignmentRequest.setStudentId(999L);

        when(awardRepository.findById(2L)).thenReturn(Optional.of(customAward));
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            awardService.assignAward(2L, assignmentRequest)
        );
        verify(assignmentRepository, never()).save(any());
    }

    // Tests for assignAward (Team)

    @Test
    @DisplayName("assignAward | Should assign to all Team members successfully")
    void testAssignAward_Team_Success() {
        assignmentRequest.setTeamId(50L);

        when(awardRepository.findById(2L)).thenReturn(Optional.of(customAward));
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));
        when(teamRepository.findById(50L)).thenReturn(Optional.of(team));

        awardService.assignAward(2L, assignmentRequest);

        verify(assignmentRepository, times(1)).save(any(AwardAssignment.class));
    }

    @Test
    @DisplayName("assignAward | Should throw exception if Team is in wrong Project")
    void testAssignAward_Team_WrongProject() {
        assignmentRequest.setTeamId(50L);

        Project otherProject = new Project();
        otherProject.setId(99L);

        when(awardRepository.findById(2L)).thenReturn(Optional.of(customAward));
        when(projectRepository.findById(10L)).thenReturn(Optional.of(otherProject));
        when(teamRepository.findById(50L)).thenReturn(Optional.of(team));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> 
            awardService.assignAward(2L, assignmentRequest)
        );
        assertEquals("Team does not belong to the specified project.", ex.getMessage());
        
        verify(assignmentRepository, never()).save(any());
    }

    @Test
    @DisplayName("assignAward | Should throw exception if neither Student nor Team provided")
    void testAssignAward_NoTarget() {
        
        when(awardRepository.findById(2L)).thenReturn(Optional.of(customAward));
        when(projectRepository.findById(10L)).thenReturn(Optional.of(project));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> 
            awardService.assignAward(2L, assignmentRequest)
        );
        assertEquals("Either studentId or teamId must be provided.", ex.getMessage());
    }

    // Tests for getAvailableAwards

    @Test
    @DisplayName("getAvailableAwards | Should return both Global and Local awards")
    void testGetAvailableAwards_Success() {
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));

        when(awardRepository.findAllByCourseIsNull()).thenReturn(List.of(globalAward));
        when(awardRepository.findAllByCourse(course)).thenReturn(List.of(customAward));

        List<AwardDTO> result = awardService.getAvailableAwards(1L);

        assertEquals(2, result.size());
        verify(awardRepository).findAllByCourseIsNull();
        verify(awardRepository).findAllByCourse(course);
    }
}