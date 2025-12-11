package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;
import com.group7.eduscrum_awards.dto.studentdashboard.StudentProjectDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.repository.CourseRepository;
import com.group7.eduscrum_awards.repository.ProjectRepository;
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

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, CourseRepository courseRepository, UserRepository userRepository) {
        this.projectRepository = projectRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
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

        if (!userRepository.existsById(studentId)) {
             throw new ResourceNotFoundException("Student not found with id: " + studentId);
        }

        // Fetch projects associated with the student from the repository
        List<Project> projects = projectRepository.findProjectsByStudentId(studentId);

        // Map to StudentProjectDTOs with filtering for the specific student
        return projects.stream()
                .map(project -> new StudentProjectDTO(project, studentId))
                .collect(Collectors.toList());
    }
}