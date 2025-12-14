package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.ProjectCourseTeamsDTO;
import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;
import com.group7.eduscrum_awards.dto.dashboard.DashboardSprintDTO;
import com.group7.eduscrum_awards.dto.dashboard.StudentDashboardDTO;
import com.group7.eduscrum_awards.dto.dashboard.StudentDashboardProjectDTO;
import com.group7.eduscrum_awards.dto.dashboard.TeacherProjectDetailsDTO;
import com.group7.eduscrum_awards.dto.dashboard.TeamDTO;
import com.group7.eduscrum_awards.dto.studentdashboard.StudentProjectDTO;
import com.group7.eduscrum_awards.dto.teacher.ProjectSummaryDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.Team;
import com.group7.eduscrum_awards.model.TeamMember;
import com.group7.eduscrum_awards.model.enums.AwardType;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.model.enums.TaskStatus;
import com.group7.eduscrum_awards.repository.AwardAssignmentRepository;
import com.group7.eduscrum_awards.repository.CourseRepository;
import com.group7.eduscrum_awards.repository.ProjectRepository;
import com.group7.eduscrum_awards.repository.TeamMemberRepository;
import com.group7.eduscrum_awards.repository.TeamRepository;
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
    private final TeamRepository teamRepository;
    private final AwardAssignmentRepository awardAssignmentRepository;

    @Autowired
    public ProjectServiceImpl(ProjectRepository projectRepository, CourseRepository courseRepository, 
                              UserRepository userRepository, TeamMemberRepository teamMemberRepository,
                              TeamRepository teamRepository, AwardAssignmentRepository awardAssignmentRepository) {
        this.projectRepository = projectRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
        this.teamMemberRepository = teamMemberRepository;
        this.teamRepository = teamRepository;
        this.awardAssignmentRepository = awardAssignmentRepository;
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

    /**
     * Retrieves all projects for courses taught by a specific teacher.
     *
     * @param teacherId The ID of the teacher.
     * @return List of ProjectSummaryDTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public List<ProjectSummaryDTO> getProjectsByTeacher(Long teacherId) {
        return projectRepository.findProjectsByTeacherId(teacherId);
    }

    /**
     * Retrieves detailed information about a specific project for teacher dashboard view.
     * Includes project info, teams, sprints, and tasks.
     *
     * @param projectId The ID of the project.
     * @return TeacherProjectDetailsDTO containing detailed project information.
     */
    @Override
    @Transactional(readOnly = true)
    public TeacherProjectDetailsDTO getProjectDetails(Long projectId) {
        // Fetch Project + Sprints + Tasks (Optimized Query)
        Project project = projectRepository.findProjectWithSprintsAndTasks(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found: " + projectId));

        // Fetch Teams separately (Simple Query)
        List<Team> teams = teamRepository.findByProjectId(projectId);

        // Assemble DTO
        TeacherProjectDetailsDTO dto = new TeacherProjectDetailsDTO();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());

        // Map Teams
        dto.setTeams(teams.stream()
                .map(TeamDTO::new)
                .collect(Collectors.toList()));

        // Map Sprints (using existing DashboardSprintDTO logic)
        List<DashboardSprintDTO> sprintDTOs = project.getSprints().stream()
                .sorted((s1, s2) -> Integer.compare(s1.getSprintNumber(), s2.getSprintNumber()))
                .map(DashboardSprintDTO::new)
                .collect(Collectors.toList());
        dto.setSprints(sprintDTOs);

        return dto;
    }
    
    /**
     * Counts the number of projects in a specific course.
     *
     * @param courseId The ID of the course.
     * @return The count of projects in the course.
     */
    @Override
    @Transactional(readOnly = true)
    public long countProjectsInCourse(Long courseId) {
        return projectRepository.countByCourseId(courseId);
    }

    /**
     * Retrieves a project by its ID.
     * 
     * @param projectId The ID of the project.
     * @return The ProjectDTO.
     * @throws ResourceNotFoundException if the project does not exist.
     */
    @Override
    @Transactional(readOnly = true)
    public ProjectDTO getProjectById(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
        return new ProjectDTO(project);
    }

    /**
     * Verifies if a teacher teaches the course associated with a specific project.
     * Helper method for security checks.
     * 
     * @param projectId The ID of the project.
     * @param teacherId The ID of the teacher.
     * @return true if the teacher has access, false otherwise.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isTeacherAllowedInProject(Long projectId, Long teacherId) {
        return projectRepository.findById(projectId)
                .map(project -> project.getCourse().getTeachers().stream()
                        .anyMatch(t -> t.getId().equals(teacherId)))
                .orElse(false);
    }

    /**
     * Retrieves the student dashboard data for the 4 cards at the top.
     * 
     * @param studentId The ID of the student.
     * @return The StudentDashboardDTO with stats and projects.
     * @throws ResourceNotFoundException if the student does not exist.
     */
    @Override
    @Transactional(readOnly = true)
    public StudentDashboardDTO getStudentDashboardWithStats(Long studentId) {

        // Validate Student existence
        Student student = (Student) userRepository.findById(studentId)
            .filter(u -> u instanceof Student)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));

        if (student.getDegree() == null) {
            throw new ResourceNotFoundException("Student with id " + studentId + " is not assigned to any degree.");
        }
        Long degreeId = student.getDegree().getId();

        // Fetch projects for the student
        List<StudentDashboardProjectDTO> projects = getStudentDashboard(studentId);

        // Assemble DTO
        StudentDashboardDTO dto = new StudentDashboardDTO();
        dto.setProjects(projects);

        // Card 1: Score
        long myScore = awardAssignmentRepository.calculateTotalScore(studentId);
        dto.setTotalScore(myScore);

        // Card 2: Awards
        dto.setTotalAwards(awardAssignmentRepository.countByStudentId(studentId));
        dto.setManualAwards(awardAssignmentRepository.countByStudentAndType(studentId, AwardType.MANUAL));
        dto.setAutomaticAwards(awardAssignmentRepository.countByStudentAndType(studentId, AwardType.AUTOMATIC));

        // Card 3: Tasks
        dto.setTasksCompleted(teamMemberRepository.countTasksByStudentTeamsAndStatus(studentId, TaskStatus.DONE));
        dto.setTasksTotal(teamMemberRepository.countAllTasksByStudentTeams(studentId));

        // Card 4: Ranking
        dto.setRanking(userRepository.calculateStudentRankInDegree(myScore, degreeId));
        dto.setTotalStudents(userRepository.countByDegreeIdAndRole(degreeId, Role.STUDENT));

        return dto;
    }

    /**
     * Retrieves the course name and number of teams for a specific project.
     *
     * @param projectId The ID of the project.
     * @return an {@link ProjectCourseTeamsDTO}.
     * @throws ResourceNotFoundException if the project does not exist.
     */
    @Override
    @Transactional(readOnly = true)
    public ProjectCourseTeamsDTO getProjectCourseAndTeamCount(Long projectId) {
        return projectRepository.findCourseNameAndTeamCountByProjectId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));
    }
}