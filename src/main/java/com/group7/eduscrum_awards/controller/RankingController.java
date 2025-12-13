package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.RankingItemDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.dto.rankings.StudentDashboardRankingDTO;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.service.RankingService;
import com.group7.eduscrum_awards.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST Controller for handling ranking-related endpoints.
 * Provides endpoints to retrieve student and team rankings.
 */
@RestController
@RequestMapping("/api/v1")
public class RankingController {

    private final RankingService rankingService;
    private final UserService userService;

    @Autowired
    public RankingController(RankingService rankingService, UserService userService) {
        this.rankingService = rankingService;
        this.userService = userService;
    }

    /**
     * Get individual student rankings for a specific Degree.
     * GET /api/v1/degrees/{degreeId}/rankings
     */
    @GetMapping("/degrees/{degreeId}/rankings")
    public ResponseEntity<List<RankingItemDTO>> getStudentRankings(@PathVariable Long degreeId) {
        return ResponseEntity.ok(rankingService.getStudentRanking(degreeId));
    }

    /**
     * Get team rankings for a specific Course.
     * GET /api/v1/courses/{courseId}/rankings/teams
     */
    @GetMapping("/courses/{courseId}/rankings/teams")
    public ResponseEntity<List<RankingItemDTO>> getTeamRankings(@PathVariable Long courseId) {
        return ResponseEntity.ok(rankingService.getTeamRanking(courseId));
    }

    /**
     * Get dashboard rankings for a specific Student.
     * GET /api/v1/students/{studentId}/rankings
     */
    @GetMapping("/students/{studentId}/rankings")
    public ResponseEntity<StudentDashboardRankingDTO> getStudentDashboardRankings(@PathVariable Long studentId,
            Principal principal) {
        
        // IDOR Protection: Ensure logged-in user matches studentId (or is Admin)
        String loggedInEmail = principal.getName();
        UserDTO currentUser = userService.getUserByEmail(loggedInEmail);
        if (!currentUser.getId().equals(studentId) && currentUser.getRole() != Role.ADMIN) {
           return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(rankingService.getStudentDashboardRankings(studentId));
    }
}