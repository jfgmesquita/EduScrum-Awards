package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.CourseCreateDTO;
import com.group7.eduscrum_awards.dto.CourseDTO;
import com.group7.eduscrum_awards.dto.CourseUpdateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.dto.teacher.ProjectSummaryDTO;
import com.group7.eduscrum_awards.service.CourseService;
import com.group7.eduscrum_awards.service.ProjectService;

import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * REST Controller for managing Courses.
 * Exposes endpoints for course creation and management.
 */
@RestController
@RequestMapping("/api/v1")
public class CourseController {

    private final CourseService courseService;
    private final ProjectService projectService;

    @Autowired
    public CourseController(CourseService courseService, ProjectService projectService) {
        this.courseService = courseService;
        this.projectService = projectService;   
    }

    /**
     * Endpoint to register a new Course and link it to a Degree.
     * Accessible via: POST /api/v1/degrees/{degreeId}/courses
     */
    @PostMapping("/degrees/{degreeId}/courses")
    public ResponseEntity<CourseDTO> registerCourseForDegree(@PathVariable Long degreeId,
            @Valid @RequestBody CourseCreateDTO courseCreateDTO) {
        
        CourseDTO newCourse = courseService.registerCourseForDegree(degreeId, courseCreateDTO);
        return new ResponseEntity<>(newCourse, HttpStatus.CREATED);
    }
    
    /**
     * Endpoint to assign an existing Teacher to an existing Course.
     * Accessible via: POST /api/v1/courses/{courseId}/teachers/{teacherId}
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

    /**
     * Endpoint to enroll an existing Student in an existing Course.
     * Accessible via: POST /api/v1/courses/{courseId}/students/{studentId}
     *
     * @param courseId The ID of the course.
     * @param studentId The ID of the student to be enrolled.
     * @return A ResponseEntity containing the updated CourseDTO and HTTP status 200.
     */
    @PostMapping("/courses/{courseId}/students/{studentId}")
    public ResponseEntity<CourseDTO> addStudentToCourse(@PathVariable Long courseId, @PathVariable Long studentId) {
        
        CourseDTO updatedCourse = courseService.addStudentToCourse(courseId, studentId);
        // Return 200 OK with the updated course
        return new ResponseEntity<>(updatedCourse, HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve all Courses.
     * Accessible via: GET /api/v1/courses
     * 
     * @return A ResponseEntity containing the list of CourseDTOs and HTTP status 200.
     */
    @GetMapping("/courses")
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    /**
     * Endpoint to retrieve all Courses associated with a specific Degree.
     * Accessible via: GET /api/v1/degrees/{degreeId}/courses
     * 
     * @param degreeId The ID of the Degree.
     * @return A ResponseEntity containing the list of CourseDTOs and HTTP status 200.
     */
    @GetMapping("/degrees/{degreeId}/courses")
    public ResponseEntity<List<CourseDTO>> getCoursesByDegree(@PathVariable Long degreeId) {
        return ResponseEntity.ok(courseService.getCoursesByDegree(degreeId));
    }

    /**
     * Endpoint to retrieve all Courses taught by a specific Teacher.
     * Accessible via: GET /api/v1/teachers/{teacherId}/courses
     * 
     * @param teacherId The ID of the Teacher.
     * @return A ResponseEntity containing the list of CourseDTOs and HTTP status 200.
     */
    @GetMapping("/teachers/{teacherId}/courses")
    public ResponseEntity<List<CourseDTO>> getCoursesByTeacher(@PathVariable Long teacherId) {
        return ResponseEntity.ok(courseService.getCoursesByTeacher(teacherId));
    }

    /**
     * Endpoint to retrieve all Students enrolled in a specific Course.
     * Accessible via: GET /api/v1/courses/{courseId}/students
     * 
     * @param courseId The ID of the Course.
     * @return A ResponseEntity containing the list of UserDTOs and HTTP status 200.
     */
    @GetMapping("/courses/{courseId}/students")
    public ResponseEntity<List<UserDTO>> getStudentsInCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getStudentsInCourse(courseId));
    }

    /**
     * Endpoint to retrieve a summary of all Projects associated with a specific Course.
     * Accessible via: GET /api/v1/courses/{courseId}/projects/summary
     * 
     * @param courseId The ID of the Course.
     * @return A ResponseEntity containing the list of ProjectSummaryDTOs and HTTP status 200.
     */
    @GetMapping("/courses/{courseId}/projects/summary")
    public ResponseEntity<List<ProjectSummaryDTO>> getProjectsSummary(@PathVariable Long courseId) {
        return ResponseEntity.ok(projectService.getProjectsSummary(courseId));
    }

    /**
     * Endpoint to update an existing Course.
     * Accessible via: PUT /api/v1/courses/{id}
     * 
     * @param id The ID of the Course to be updated.
     * @param dto The updated data for the Course, passed in the request body.
     * @return A ResponseEntity containing the updated CourseDTO and HTTP status 200.
     */
    @PutMapping("/courses/{id}")
    public ResponseEntity<CourseDTO> updateCourse(@PathVariable Long id, @Valid @RequestBody CourseUpdateDTO dto) {
        return ResponseEntity.ok(courseService.updateCourse(id, dto));
    }
}