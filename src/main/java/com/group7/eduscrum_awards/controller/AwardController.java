package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.AwardAssignmentRequestDTO;
import com.group7.eduscrum_awards.dto.AwardCreateDTO;
import com.group7.eduscrum_awards.dto.AwardDTO;
import com.group7.eduscrum_awards.dto.StudentAwardDTO;
import com.group7.eduscrum_awards.service.AwardService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for managing Awards.
 * Allows teachers to create custom awards and assign awards to students/teams.
 */
@RestController
@RequestMapping("/api/v1")
public class AwardController {

    private final AwardService awardService;

    @Autowired
    public AwardController(AwardService awardService) {
        this.awardService = awardService;
    }

    /**
     * Endpoint for a Teacher to create a custom Award for a specific Course.
     * Accessible via: POST /api/v1/courses/{courseId}/awards
     */
    @PostMapping("/courses/{courseId}/awards")
    public ResponseEntity<AwardDTO> createCustomAward(@PathVariable Long courseId,
            @Valid @RequestBody AwardCreateDTO createDTO) {
        
        AwardDTO newAward = awardService.createCustomAward(courseId, createDTO);
        return new ResponseEntity<>(newAward, HttpStatus.CREATED);
    }

    /**
     * Endpoint to list all available awards for a course (Global + Local).
     * Accessible via: GET /api/v1/courses/{courseId}/awards
     */
    @GetMapping("/courses/{courseId}/awards")
    public ResponseEntity<List<AwardDTO>> getAvailableAwards(@PathVariable Long courseId) {
        List<AwardDTO> awards = awardService.getAvailableAwards(courseId);
        return ResponseEntity.ok(awards);
    }

    /**
     * Endpoint for a Teacher to assign an Award to a Student or Team.
     * Accessible via: POST /api/v1/awards/{awardId}/assign
     */
    @PostMapping("/awards/{awardId}/assign")
    public ResponseEntity<Void> assignAward(@PathVariable Long awardId,
            @Valid @RequestBody AwardAssignmentRequestDTO requestDTO) {
        
        awardService.assignAward(awardId, requestDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * Endpoint to get the portfolio of badges earned by a student.
     * Accessible via: GET /api/v1/students/{studentId}/awards
     */
    @GetMapping("/students/{studentId}/awards")
    public ResponseEntity<List<StudentAwardDTO>> getStudentAwards(
            @PathVariable Long studentId) {
        
        List<StudentAwardDTO> awards = awardService.getStudentAwards(studentId);
        return ResponseEntity.ok(awards);
    }
}