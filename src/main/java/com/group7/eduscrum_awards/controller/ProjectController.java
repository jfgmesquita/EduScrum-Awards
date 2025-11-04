package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;
import com.group7.eduscrum_awards.service.ProjectService;
import jakarta.validation.Valid;
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
     * Accessible via: POST http://localhost:8080/api/v1/courses/{courseId}/projects
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
}