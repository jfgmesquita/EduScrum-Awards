package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.RankingItemDTO;
import com.group7.eduscrum_awards.repository.AwardAssignmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RankingServiceImplTest {

    @Mock
    private AwardAssignmentRepository assignmentRepository;

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
}