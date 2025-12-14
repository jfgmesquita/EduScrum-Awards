package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.ProjectCourseTeamsDTO;
import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.dto.dashboard.StudentDashboardDTO;
import com.group7.eduscrum_awards.dto.dashboard.StudentDashboardProjectDTO;
import com.group7.eduscrum_awards.dto.dashboard.TeacherProjectDetailsDTO;
import com.group7.eduscrum_awards.dto.teacher.ProjectSummaryDTO;
import com.group7.eduscrum_awards.model.enums.Role;
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
     * Endpoint for the Student Dashboard.
     * Returns all projects, roles, sprints, and tasks for the logged-in student.
     * Accessible via: GET /api/v1/students/{studentId}/projects
     *
     * @param studentId The ID of the student.
     * @param principal The security principal (to verify identity).
     * @return List of detailed project info.
     */
    @GetMapping("/students/{studentId}/projects")
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

    /**
     * Endpoint to retrieve all projects created by a specific teacher.
     * Accessible via: GET /api/v1/teachers/{teacherId}/projects
     *
     * @param teacherId The ID of the teacher (from the URL path).
     * @param principal The security principal (to verify identity).
     * @return A ResponseEntity containing a list of ProjectSummaryDTOs.
     */
    @GetMapping("/teachers/{teacherId}/projects")
    public ResponseEntity<List<ProjectSummaryDTO>> getProjectsByTeacher(
            @PathVariable Long teacherId,
            Principal principal) {
        
        // IDOR Check: Ensure logged user matches the requested teacherId
        String email = principal.getName();
        UserDTO user = userService.getUserByEmail(email);
        
        if (!user.getId().equals(teacherId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        return ResponseEntity.ok(projectService.getProjectsByTeacher(teacherId));
    }

    /**
     * Endpoint to retrieve all projects within a specific course.
     * Accessible via: GET /api/v1/courses/{courseId}/projects
     *
     * @param courseId The ID of the course (from the URL path).
     * @return A ResponseEntity containing a list of ProjectSummaryDTOs.
     */
    @GetMapping("/courses/{courseId}/projects")
    public ResponseEntity<List<ProjectSummaryDTO>> getProjectsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(projectService.getProjectsSummary(courseId));
    }

    /**
     * Endpoint to get the count of projects within a specific course.
     * Accessible via: GET /api/v1/courses/{courseId}/projects/count
     *
     * @param courseId The ID of the course (from the URL path).
     * @return A ResponseEntity containing the count of projects.
     */
    @GetMapping("/courses/{courseId}/projects/count")
    public ResponseEntity<Long> getProjectCount(@PathVariable Long courseId) {
        return ResponseEntity.ok(projectService.countProjectsInCourse(courseId));
    }

    /**
     * Endpoint to retrieve detailed information about a specific project for teachers.
     * Accessible via: GET /api/v1/projects/{projectId}/details
     *
     * @param projectId The ID of the project (from the URL path).
     * @return A ResponseEntity containing TeacherProjectDetailsDTO.
     */
    @GetMapping("/projects/{projectId}/details")
    public ResponseEntity<TeacherProjectDetailsDTO> getProjectDetails(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectDetails(projectId));
    }

    /**
     * Endpoint to retrieve a project by its ID.
     * Accessible via: GET /api/v1/projects/{id}
     *
     * @param id The ID of the project (from the URL path).
     * @param principal The security principal (to verify identity).
     * @return A ResponseEntity containing the ProjectDTO.
     */
    @GetMapping("/projects/{id}")
    public ResponseEntity<ProjectDTO> getProjectById(@PathVariable Long id, Principal principal) {
        
        // Fetch user info for Security Check
        String email = principal.getName();
        UserDTO user = userService.getUserByEmail(email);

        // IDOR Protection (Teacher Context)
        // If the user is a TEACHER, verify they teach the course of this project
        if (user.getRole() == Role.TEACHER) {
            boolean isAllowed = projectService.isTeacherAllowedInProject(id, user.getId());
            if (!isAllowed) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        }

        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    /**
     * Endpoint to retrieve the student dashboard data including stats for the 4 cards at the top.
     * Accessible via: GET /api/v1/students/{studentId}/dashboard
     *
     * @param studentId The ID of the student.
     * @param principal The security principal (to verify identity).
     * @return A ResponseEntity containing the StudentDashboardDTO with stats and projects.
     */
    @GetMapping("/students/{studentId}/dashboard")
    public ResponseEntity<StudentDashboardDTO> getStudentStats(
        @PathVariable Long studentId, Principal principal) {
        
        String loggedEmail = principal.getName();
        UserDTO loggedUser = userService.getUserByEmail(loggedEmail);
        if (!loggedUser.getId().equals(studentId)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    
        StudentDashboardDTO dashboard = projectService.getStudentDashboardWithStats(studentId);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Endpoint to retrieve the course name and number of teams for a specific project.
     * Accessible via: GET /api/v1/projects/{projectId}/course-teams
     *
     * @param projectId The ID of the project.
     * @return A ResponseEntity containing the ProjectCourseTeamsDTO.
     */
    @GetMapping("/projects/{projectId}/course-teams")
    public ResponseEntity<ProjectCourseTeamsDTO> getProjectCourseAndTeamCount(@PathVariable Long projectId) {
        return ResponseEntity.ok(projectService.getProjectCourseAndTeamCount(projectId));
    }
}