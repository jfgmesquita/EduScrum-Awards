package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.CourseCreateDTO;
import com.group7.eduscrum_awards.dto.CourseDTO;
import com.group7.eduscrum_awards.service.CourseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * REST Controller for managing Courses.
 * Exposes endpoints for course creation and management.
 */
@RestController
@RequestMapping("/api/v1")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    /**
     * Endpoint to register a new Course and link it to a Degree.
     * Accessible via: POST http://localhost:8080/api/v1/degrees/{degreeId}/courses
     */
    @PostMapping("/degrees/{degreeId}/courses")
    public ResponseEntity<CourseDTO> registerCourseForDegree(@PathVariable Long degreeId,
            @Valid @RequestBody CourseCreateDTO courseCreateDTO) {
        
        CourseDTO newCourse = courseService.registerCourseForDegree(degreeId, courseCreateDTO);
        return new ResponseEntity<>(newCourse, HttpStatus.CREATED);
    }
    
    /**
     * Endpoint to assign an existing Teacher to an existing Course.
     * Accessible via: POST http://localhost:8080/api/v1/courses/{courseId}/teachers/{teacherId}
     *
     * @param courseId The ID of the course to be updated.
     * @param teacherId The ID of the teacher to be assigned.
     * @return A ResponseEntity containing the updated CourseDTO and HTTP status 200.
     */
    @PostMapping("/courses/{courseId}/teachers/{teacherId}")
    public ResponseEntity<CourseDTO> addTeacherToCourse(@PathVariable Long courseId, @PathVariable Long teacherId) {
        
        CourseDTO updatedCourse = courseService.addTeacherToCourse(courseId, teacherId);
        // Return 200 OK with the updated course
        return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
    }
}