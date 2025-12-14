package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.RankingItemDTO;
import com.group7.eduscrum_awards.dto.rankings.StudentDashboardRankingDTO;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Degree;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.repository.AwardAssignmentRepository;
import com.group7.eduscrum_awards.repository.UserRepository;
import com.group7.eduscrum_awards.repository.projection.StudentScoreSummary;
import com.group7.eduscrum_awards.repository.projection.TeamScoreSummary;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

/** Unit tests for RankingServiceImpl. */
@ExtendWith(MockitoExtension.class)
class RankingServiceImplTest {

    @Mock
    private AwardAssignmentRepository assignmentRepository;

    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private RankingServiceImpl rankingService;

    @Test
    @DisplayName("getStudentRanking | Should return list of student rankings")
    void testGetStudentRanking() {
 
        Long degreeId = 1L;
        List<RankingItemDTO> mockRankings = Arrays.asList(
            new RankingItemDTO("Alice", 50.0),
            new RankingItemDTO("Bob", 30.0)
        );

        when(assignmentRepository.findStudentRankingByDegree(degreeId)).thenReturn(mockRankings);

        List<RankingItemDTO> result = rankingService.getStudentRanking(degreeId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Alice", result.get(0).getName());
        verify(assignmentRepository, times(1)).findStudentRankingByDegree(degreeId);
    }

    @Test
    @DisplayName("getTeamRanking | Should return list of team rankings")
    void testGetTeamRanking() {

        Long courseId = 10L;
        List<RankingItemDTO> mockRankings = Arrays.asList(
            new RankingItemDTO("Alpha Team", 10.0),
            new RankingItemDTO("Beta Team", 8.5)
        );

        when(assignmentRepository.findTeamRankingByCourse(courseId)).thenReturn(mockRankings);

        List<RankingItemDTO> result = rankingService.getTeamRanking(courseId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(10.0, result.get(0).getTotalScore());
        verify(assignmentRepository, times(1)).findTeamRankingByCourse(courseId);
    }

    @Test
    @DisplayName("getStudentDashboardRankings | Should assemble data correctly")
    void testGetStudentDashboardRankings() {
        Long studentId = 1L;
        Long degreeId = 10L;
        
        Degree degree = new Degree();
        degree.setId(degreeId);
        degree.setName("Computer Science");

        Student student = new Student();
        student.setId(studentId);
        student.setDegree(degree);
        student.setCourses(new java.util.HashSet<>());
        
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        
        lenient().when(assignmentRepository.findGlobalStudentRankings()).thenReturn(List.of());
        
        lenient().when(assignmentRepository.findStudentRankingByDegree(degreeId)).thenReturn(List.of());

        var result = rankingService.getStudentDashboardRankings(studentId);
        
        assertNotNull(result);
        assertNotNull(result.getIndividualRankings());
    }

    @Test
    @DisplayName("getStudentDashboardRankings | Should assemble full ranking data with courses and teams")
    void testGetStudentDashboardRankings_Success() {
        Long studentId = 1L;
        Long degreeId = 10L;
        Long courseId = 100L;
        Long teamId = 50L;

        Degree degree = new Degree();
        degree.setId(degreeId);
        degree.setName("Computer Science");

        Course course = new Course();
        course.setId(courseId);
        course.setName("Software Engineering");

        Student student = new Student();
        student.setId(studentId);
        student.setName("Alice");
        student.setDegree(degree);
        student.setCourses(new HashSet<>(Set.of(course)));

        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));

        StudentScoreSummary studentSummary = mock(StudentScoreSummary.class);
        when(studentSummary.getStudentId()).thenReturn(studentId);
        when(studentSummary.getStudentName()).thenReturn("Alice");
        when(studentSummary.getTotalScore()).thenReturn(100L);

        when(assignmentRepository.findStudentRankingsByDegreeId(degreeId))
            .thenReturn(List.of(studentSummary));

        TeamScoreSummary teamSummary = mock(TeamScoreSummary.class);
        when(teamSummary.getTeamId()).thenReturn(teamId);
        when(teamSummary.getTeamName()).thenReturn("Alpha Team");
        
        when(teamSummary.getTotalScore()).thenReturn(50L); 

        when(assignmentRepository.findTeamRankingsByCourseId(courseId))
            .thenReturn(List.of(teamSummary));

        StudentScoreSummary memberSummary = mock(StudentScoreSummary.class);
        when(memberSummary.getStudentId()).thenReturn(studentId);
        when(memberSummary.getStudentName()).thenReturn("Alice");
        when(memberSummary.getTotalScore()).thenReturn(50L);

        when(assignmentRepository.findTeamMemberScores(teamId))
            .thenReturn(List.of(memberSummary));

        StudentDashboardRankingDTO result = rankingService.getStudentDashboardRankings(studentId);

        assertNotNull(result);
        
        assertEquals(1, result.getIndividualRankings().size());
        assertEquals("Alice", result.getIndividualRankings().get(0).getStudentName());

        assertEquals(1, result.getTeamRankingsByCourse().size());
        assertEquals("Software Engineering", result.getTeamRankingsByCourse().get(0).getCourseName());
        
        assertEquals(1, result.getTeamRankingsByCourse().get(0).getRankings().size());
        assertEquals("Alpha Team", result.getTeamRankingsByCourse().get(0).getRankings().get(0).getTeamName());
        
        verify(userRepository).findById(studentId);
        verify(assignmentRepository).findStudentRankingsByDegreeId(degreeId);
        verify(assignmentRepository).findTeamRankingsByCourseId(courseId);
        verify(assignmentRepository).findTeamMemberScores(teamId);
    }
}