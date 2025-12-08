package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.RankingItemDTO;
import com.group7.eduscrum_awards.service.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for handling ranking-related endpoints.
 * Provides endpoints to retrieve student and team rankings.
 */
@RestController
@RequestMapping("/api/v1")
public class RankingController {

    private final RankingService rankingService;

    @Autowired
    public RankingController(RankingService rankingService) {
        this.rankingService = rankingService;
    }

    /**
     * Get individual student rankings for a specific Degree.
     * GET http://localhost:8080/api/v1/degrees/{degreeId}/rankings
     */
    @GetMapping("/degrees/{degreeId}/rankings")
    public ResponseEntity<List<RankingItemDTO>> getStudentRankings(@PathVariable Long degreeId) {
        return ResponseEntity.ok(rankingService.getStudentRanking(degreeId));
    }

    /**
     * Get team rankings for a specific Course.
     * GET http://localhost:8080/api/v1/courses/{courseId}/rankings/teams
     */
    @GetMapping("/courses/{courseId}/rankings/teams")
    public ResponseEntity<List<RankingItemDTO>> getTeamRankings(@PathVariable Long courseId) {
        return ResponseEntity.ok(rankingService.getTeamRanking(courseId));
    }
}