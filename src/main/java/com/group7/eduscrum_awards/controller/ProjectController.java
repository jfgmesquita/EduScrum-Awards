package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.dto.dashboard.StudentDashboardProjectDTO;
import com.group7.eduscrum_awards.dto.studentdashboard.StudentProjectDTO;
import com.group7.eduscrum_awards.service.ProjectService;
import com.group7.eduscrum_awards.service.UserService;

import jakarta.validation.Valid;

import java.security.Principal;
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
    private final UserService userService;

    @Autowired
    public ProjectController(ProjectService projectService, UserService userService) {
        this.projectService = projectService;
        this.userService = userService;
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
    @GetMapping("   ")
    public ResponseEntity<List<StudentProjectDTO>> getStudentProjects(
            @PathVariable Long studentId, Principal principal) {

        // Check who is the logged-in user
        String loggedUsername = principal.getName();
        UserDTO loggedUser = userService.getUserByEmail(loggedUsername);
        
        // Validate if the logged-in user is the owner of the data
        boolean isOwner = loggedUser.getId().equals(studentId);

        if (!isOwner) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(projectService.getMyProjects(studentId));
    }

    /**
     * Endpoint for the Student Dashboard.
     * Returns all projects, roles, sprints, and tasks for the logged-in student.
     * Accessible via: GET /api/v1/students/{studentId}/dashboard
     *
     * @param studentId The ID of the student.
     * @param principal The security principal (to verify identity).
     * @return List of detailed project info.
     */
    @GetMapping("/students/{studentId}/dashboard")
    public ResponseEntity<List<StudentDashboardProjectDTO>> getStudentDashboard(@PathVariable Long studentId,
            Principal principal) {

        // Security Check: IDOR Protection
        String loggedUsername = principal.getName();
        UserDTO loggedUser = userService.getUserByEmail(loggedUsername);

        if (!loggedUser.getId().equals(studentId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(projectService.getStudentDashboard(studentId));
    }
}