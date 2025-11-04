package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.CourseCreateDTO;
import com.group7.eduscrum_awards.dto.CourseDTO;
import com.group7.eduscrum_awards.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for managing Courses.
 * Exposes endpoints for course creation and management.
 */
@RestController
@RequestMapping("/api/v1/courses")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Endpoint to register a new Course.
     * Accessible via: POST http://localhost:8080/api/v1/courses
     *
     * @param courseCreateDTO The course data from the request body.
     * @return A ResponseEntity containing the created CourseDTO and HTTP status 201.
     * @Valid triggers validation rules from the DTO
     * @RequestBody converts the JSON body into the CourseCreateDTO object
     */
    @PostMapping
    public ResponseEntity<CourseDTO> registerCourse(@Valid @RequestBody CourseCreateDTO courseCreateDTO) {

        CourseDTO newCourse = courseService.registerCourse(courseCreateDTO);

        // Return the "safe" DTO and a 201 Created status
        return new ResponseEntity<>(newCourse, HttpStatus.CREATED);
    }
}