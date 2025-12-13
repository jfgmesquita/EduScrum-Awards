package com.group7.eduscrum_awards.service;

import com.group7.eduscrum_awards.dto.RankingItemDTO;
import com.group7.eduscrum_awards.dto.rankings.StudentDashboardRankingDTO;

import java.util.List;

/** 
 * Service interface for retrieving student and team rankings.
 * Defines methods to get rankings based on degree and course.
 */
public interface RankingService {

    /**
     * Gets the student ranking for a specific degree.
     * 
     * @param degreeId
     * @return List of RankingItemDTO representing student rankings.
     */
    List<RankingItemDTO> getStudentRanking(Long degreeId);

    /**
     * Gets the team ranking for a specific course.
     * 
     * @param courseId
     * @return List of RankingItemDTO representing team rankings.
     */
    List<RankingItemDTO> getTeamRanking(Long courseId);

    /**
     * Gets the dashboard rankings for a specific student.
     * 
     * @param studentId
     * @return StudentDashboardRankingDTO containing individual and team rankings.
     */
    StudentDashboardRankingDTO getStudentDashboardRankings(Long studentId);
}