package com.group7.eduscrum_awards.service;

import java.util.List;

import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;
import com.group7.eduscrum_awards.dto.dashboard.StudentDashboardDTO;
import com.group7.eduscrum_awards.dto.dashboard.StudentDashboardProjectDTO;
import com.group7.eduscrum_awards.dto.dashboard.TeacherProjectDetailsDTO;
import com.group7.eduscrum_awards.dto.studentdashboard.StudentProjectDTO;
import com.group7.eduscrum_awards.dto.teacher.ProjectSummaryDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;

/**
 * Service Interface (Contract) for Project operations.
 * Defines business logic methods for managing projects.
 */
public interface ProjectService {

    /**
     * Creates a new Project and associates it with a specific Course.
     * This operation is intended to be called by a Teacher.
     *
     * @param courseId The ID of the Course this project will belong to.
     * @param projectCreateDTO The DTO containing the data for the new Project.
     * @return The DTO of the newly created Project.
     * @throws ResourceNotFoundException if the courseId does not exist.
     * @throws DuplicateResourceException if a Project with the same name already exists in that Course.
     */
    ProjectDTO createProject(Long courseId, ProjectCreateDTO projectCreateDTO);

    /**
     * Retrieves all projects associated with a specific student.
     * Each project includes only the sprints and tasks relevant to that student.
     * 
     * @param studentId The ID of the student.
     * @return A list of StudentProjectDTOs representing the student's projects.
     * @throws ResourceNotFoundException if the student with the given ID is not found.
     */
    List<StudentProjectDTO> getMyProjects(Long studentId);

    /**
     * Retrieves a summary of projects for a specific course, including the number of teams in each project.
     * 
     * @param courseId The ID of the course.
     * @return A list of ProjectSummaryDTOs representing the projects and their team counts.
     */
    public List<ProjectSummaryDTO> getProjectsSummary(Long courseId);

    /**
     * Retrieves all projects for a specific student, including their role,
     * team name, and all associated sprints and tasks.
     *
     * @param studentId The ID of the student.
     * @return List of dashboard DTOs.
     */
    List<StudentDashboardProjectDTO> getStudentDashboard(Long studentId);

    /**
     * Retrieves all projects for courses taught by a specific teacher.
     *
     * @param teacherId The ID of the teacher.
     * @return List of ProjectSummaryDTOs.
     */
    List<ProjectSummaryDTO> getProjectsByTeacher(Long teacherId);

    /**
     * Retrieves detailed information about a specific project for teacher dashboard view.
     * Includes project info, teams, sprints, and tasks.
     *
     * @param projectId The ID of the project.
     * @return TeacherProjectDetailsDTO containing detailed project information.
     * @throws ResourceNotFoundException if the project with the given ID is not found.
     */
    TeacherProjectDetailsDTO getProjectDetails(Long projectId);
    
    /**
     * Counts the number of projects in a specific course.
     *
     * @param courseId The ID of the course.
     * @return The count of projects in the course.
     */
    long countProjectsInCourse(Long courseId);

    /**
     * Retrieves a project by its ID.
     * 
     * @param projectId The ID of the project.
     * @return The ProjectDTO.
     * @throws ResourceNotFoundException if the project does not exist.
     */
    ProjectDTO getProjectById(Long projectId);
    
    /**
     * Verifies if a teacher teaches the course associated with a specific project.
     * Helper method for security checks.
     * 
     * @param projectId The ID of the project.
     * @param teacherId The ID of the teacher.
     * @return true if the teacher has access, false otherwise.
     */
    boolean isTeacherAllowedInProject(Long projectId, Long teacherId);

    /**
     * Retrieves the student dashboard data for the 4 cards at the top.
     * 
     * @param studentId The ID of the student.
     * @return The StudentDashboardDTO with stats and projects.
     * @throws ResourceNotFoundException if the student does not exist.
     */
    public StudentDashboardDTO getStudentDashboardWithStats(Long studentId);
}