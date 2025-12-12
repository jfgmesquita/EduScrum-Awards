package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.stats.*;
import com.group7.eduscrum_awards.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for statistics endpoints.
 * Handles requests for global, degree, course, and teacher statistics.
 */
@RestController
@RequestMapping("/api/v1/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    /**
     * Endpoint to retrieve global statistics.
     * Accessible via: GET /api/v1/stats/global
     */
    @GetMapping("/global")
    public ResponseEntity<GlobalStatsDTO> getGlobalStats() {
        return ResponseEntity.ok(statsService.getGlobalStats());
    }

    /**
     * Endpoint to retrieve degree-specific statistics.
     * Accessible via: GET /api/v1/stats/degrees/{degreeId}
     */
    @GetMapping("/degrees/{degreeId}")
    public ResponseEntity<DegreeStatsDTO> getDegreeStats(@PathVariable Long degreeId) {
        return ResponseEntity.ok(statsService.getDegreeStats(degreeId));
    }

    /**
     * Endpoint to retrieve course-specific statistics.
     * Accessible via: GET /api/v1/stats/courses/{courseId}
     */
    @GetMapping("/courses/{courseId}")
    public ResponseEntity<CourseStatsDTO> getCourseStats(@PathVariable Long courseId) {
        return ResponseEntity.ok(statsService.getCourseStats(courseId));
    }

    /**
     * Endpoint to retrieve teacher-specific statistics.
     * Accessible via: GET /api/v1/stats/teachers/{teacherId}
     */
    @GetMapping("/teachers/{teacherId}")
    public ResponseEntity<TeacherStatsDTO> getTeacherStats(@PathVariable Long teacherId) {
        return ResponseEntity.ok(statsService.getTeacherStats(teacherId));
    }
}