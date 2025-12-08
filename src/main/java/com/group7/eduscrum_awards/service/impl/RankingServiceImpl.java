package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.RankingItemDTO;
import com.group7.eduscrum_awards.repository.AwardAssignmentRepository;
import com.group7.eduscrum_awards.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RankingServiceImpl implements RankingService {

    private final AwardAssignmentRepository assignmentRepository;

    /**
     * Constructor for RankingServiceImpl.
     * 
     * @param assignmentRepository
     */
    @Autowired
    public RankingServiceImpl(AwardAssignmentRepository assignmentRepository) {
        this.assignmentRepository = assignmentRepository;
    }

    /**
     * Gets the student ranking for a specific degree.
     * 
     * @param degreeId
     * @return List of RankingItemDTO representing student rankings.
     */
    @Override
    @Transactional(readOnly = true)
    public List<RankingItemDTO> getStudentRanking(Long degreeId) {
        return assignmentRepository.findStudentRankingByDegree(degreeId);
    }

    /**
     * Gets the team ranking for a specific course.
     * 
     * @param courseId
     * @return List of RankingItemDTO representing team rankings.
     */
    @Override
    @Transactional(readOnly = true)
    public List<RankingItemDTO> getTeamRanking(Long courseId) {
        return assignmentRepository.findTeamRankingByCourse(courseId);
    }
}