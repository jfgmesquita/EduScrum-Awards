package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;
import com.group7.eduscrum_awards.dto.dashboard.StudentDashboardProjectDTO;
import com.group7.eduscrum_awards.dto.studentdashboard.StudentProjectDTO;
import com.group7.eduscrum_awards.dto.teacher.ProjectSummaryDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.TeamMember;
import com.group7.eduscrum_awards.repository.CourseRepository;
import com.group7.eduscrum_awards.repository.ProjectRepository;
import com.group7.eduscrum_awards.repository.TeamMemberRepository;
import com.group7.eduscrum_awards.repository.UserRepository;
import com.group7.eduscrum_awards.service.ProjectService;

import java.util.stream.Collectors;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the ProjectService.
 * Handles the logic for creating projects.
 */
@Service
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, CourseRepository courseRepository, 
                              UserRepository userRepository, TeamMemberRepository teamMemberRepository) {
        this.projectRepository = projectRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    /** Creates a new project for a specific course. */
    @Override
    @Transactional
    public ProjectDTO createProject(Long courseId, ProjectCreateDTO projectCreateDTO) {
        
        // Find the parent Course
        Course course = courseRepository.findById(courseId)
            .orElseThrow(() -> new ResourceNotFoundException("Course not found with id: " + courseId));

        // Check for duplicate project names within this course
        projectRepository.findByNameAndCourse(projectCreateDTO.getName(), course)
            .ifPresent(p -> {
                throw new DuplicateResourceException("A Project with the name '" + projectCreateDTO.getName() + "' already exists in this course.");
            });

        // Create the new Project entity
        Project newProject = new Project(
            projectCreateDTO.getName(),
            projectCreateDTO.getDescription(),
            course,
            projectCreateDTO.getStartDate(),
            projectCreateDTO.getEndDate()
        );

        // Save the new project - the "Many" side (Project) which holds the @JoinColumn
        Project savedProject = projectRepository.save(newProject);

        // Return the DTO
        return new ProjectDTO(savedProject);
    }

    /**
     * Retrieves all projects associated with a specific student.
     * Each project includes only the sprints and tasks relevant to that student.
     * 
     * @param studentId The ID of the student.
     * @return A list of StudentProjectDTOs representing the student's projects.
     */
    @Override
    @Transactional(readOnly = true)
    public List<StudentProjectDTO> getMyProjects(Long studentId) {

        userRepository.findById(studentId)
            .filter(user -> user instanceof Student)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // Fetch projects associated with the student from the repository
        List<Project> projects = projectRepository.findProjectsByStudentId(studentId);

        // Map to StudentProjectDTOs with filtering for the specific student
        return projects.stream()
                .map(project -> new StudentProjectDTO(project, studentId))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a summary of projects for a specific course, including the number of teams in each project.
     * 
     * @param courseId The ID of the course.
     * @return A list of ProjectSummaryDTOs representing the projects and their team counts.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectSummaryDTO> getProjectsSummary(Long courseId) {
        return projectRepository.findProjectsWithTeamCount(courseId);
    }

    /**
     * Retrieves all projects for a specific student, including their role,
     * team name, and all associated sprints and tasks.
     *
     * @param studentId The ID of the student.
     * @return List of dashboard DTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public List<StudentDashboardProjectDTO> getStudentDashboard(Long studentId) {
        userRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        List<TeamMember> memberships = teamMemberRepository.findByStudentId(studentId);

        return memberships.stream()
            .map(member -> {
                Project project = member.getTeam().getProject();
                return new StudentDashboardProjectDTO(
                    project,
                    member.getTeamRole(),
                    member.getTeam().getName()
                );
            })
            .sorted(java.util.Comparator.comparing(dto -> dto.getProjectName()))
            .collect(Collectors.toList());
    }
}