package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;
import com.group7.eduscrum_awards.dto.studentdashboard.StudentProjectDTO;
import com.group7.eduscrum_awards.service.ProjectService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing Projects.
 * Exposes endpoints for project creation by Teachers.
 */
@RestController
@RequestMapping("/api/v1")
public class ProjectController {

    private final ProjectService projectService;

    @Autowired
    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * Endpoint to create a new Project within a specific Course.
     * This endpoint is intended to be called by a Teacher.
     * Accessible via: POST /api/v1/courses/{courseId}/projects
     *
     * @param courseId The ID of the parent Course (from the URL path).
     * @param projectCreateDTO The project data from the request body.
     * @return A ResponseEntity containing the created ProjectDTO and HTTP status 201.
     */
    @PostMapping("/courses/{courseId}/projects")
    public ResponseEntity<ProjectDTO> createProject(@PathVariable Long courseId,
            @Valid @RequestBody ProjectCreateDTO projectCreateDTO) {
        
        ProjectDTO newProject = projectService.createProject(courseId, projectCreateDTO);
        // Return the new project and a 201 Created status
        return new ResponseEntity<>(newProject, HttpStatus.CREATED);
    }

    /**
     * Endpoint to retrieve all projects associated with a specific student.
     * Each project includes only the sprints and tasks relevant to that student.
     * Accessible via: GET /api/v1/students/{studentId}/projects
     *
     * @param studentId The ID of the student (from the URL path).
     * @return A ResponseEntity containing a list of StudentProjectDTOs.
     */
    @GetMapping("/students/{studentId}/projects")
    public ResponseEntity<List<StudentProjectDTO>> getStudentProjects(@PathVariable Long studentId) {
        return ResponseEntity.ok(projectService.getMyProjects(studentId));
    }
}