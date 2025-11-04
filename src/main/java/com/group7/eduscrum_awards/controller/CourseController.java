package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.CourseCreateDTO;
import com.group7.eduscrum_awards.dto.CourseDTO;
import com.group7.eduscrum_awards.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*; // Importar @PathVariable

/**
 * REST Controller for managing Courses.
 * Exposes endpoints for course creation and management.
 */
@RestController
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Endpoint to register a new Course and link it to a Degree.
     * Accessible via: POST http://localhost:8080/api/v1/degrees/{degreeId}/courses
     *
     * @param degreeId The ID of the parent Degree (from the URL path).
     * @param courseCreateDTO The course data (name) from the request body.
     * @return A ResponseEntity containing the created CourseDTO and HTTP status 201.
     */
    @PostMapping("/api/v1/degrees/{degreeId}/courses")
    public ResponseEntity<CourseDTO> registerCourseForDegree(
            @PathVariable Long degreeId,
            @Valid @RequestBody CourseCreateDTO courseCreateDTO) {
        
        CourseDTO newCourse = courseService.registerCourseForDegree(degreeId, courseCreateDTO);
        // We return HttpStatus.CREATED (201) which is the standard for a successful POST operation.
        return new ResponseEntity<>(newCourse, HttpStatus.CREATED);
    }
}