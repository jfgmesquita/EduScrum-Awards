package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.ProjectCourseTeamsDTO;
import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;
import com.group7.eduscrum_awards.dto.dashboard.StudentDashboardDTO;
import com.group7.eduscrum_awards.dto.dashboard.TeacherProjectDetailsDTO;
import com.group7.eduscrum_awards.dto.studentdashboard.StudentProjectDTO;
import com.group7.eduscrum_awards.dto.studentdashboard.StudentTaskDTO;
import com.group7.eduscrum_awards.dto.teacher.ProjectSummaryDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Degree;
import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Sprint;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.Task;
import com.group7.eduscrum_awards.model.Teacher;
import com.group7.eduscrum_awards.model.Team;
import com.group7.eduscrum_awards.model.TeamMember;
import com.group7.eduscrum_awards.model.enums.AwardType;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.model.enums.TaskStatus;
import com.group7.eduscrum_awards.model.enums.TeamRole;
import com.group7.eduscrum_awards.repository.AwardAssignmentRepository;
import com.group7.eduscrum_awards.repository.CourseRepository;
import com.group7.eduscrum_awards.repository.ProjectRepository;
import com.group7.eduscrum_awards.repository.TeamMemberRepository;
import com.group7.eduscrum_awards.repository.TeamRepository;
import com.group7.eduscrum_awards.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the ProjectServiceImpl.
 * These tests isolate the service layer logic and use
 * Mockito to simulate the behavior of its repositories.
 */
@ExtendWith(MockitoExtension.class)
class ProjectServiceImplTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock 
    private UserRepository userRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private AwardAssignmentRepository awardAssignmentRepository;

    @InjectMocks
    private ProjectServiceImpl projectService;

    
    private Course existingCourse;
    private ProjectCreateDTO createDTO;
    private Project savedProject;
    private Student student;

    @BeforeEach
    void setUp() {
        // Setup base data
        existingCourse = new Course("Test Course");
        existingCourse.setId(1L);

        createDTO = new ProjectCreateDTO();
        createDTO.setName("Test Project");
        createDTO.setDescription("Desc");
        createDTO.setStartDate(LocalDate.now());
        createDTO.setEndDate(LocalDate.now().plusMonths(1));

        savedProject = new Project(createDTO.getName(), createDTO.getDescription(), existingCourse, createDTO.getStartDate(), createDTO.getEndDate());
        savedProject.setId(10L);

        student = new Student("John", "john@test.com", "pass");
        student.setId(5L);
    }

    // Tests for createProject

    @Test
    @DisplayName("createProject | Should create project successfully")
    void testCreateProject_Success() {

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        when(projectRepository.findByNameAndCourse("Test Project", existingCourse)).thenReturn(Optional.empty());
        doReturn(savedProject).when(projectRepository).save(notNull());

        ProjectDTO result = projectService.createProject(1L, createDTO);

        assertNotNull(result);
        assertEquals(10L, result.getId());
        assertEquals("Test Project", result.getName());
        
        // Validate with data defined in setUp() ("Desc")
        assertEquals("Desc", result.getDescription());
        
        // Validate with the date defined in setUp() (LocalDate.now())
        assertEquals(createDTO.getStartDate(), result.getStartDate());
        assertEquals(1L, result.getCourseId());

        verify(courseRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).findByNameAndCourse("Test Project", existingCourse);
        verify(projectRepository, times(1)).save(notNull());
    }

    @Test
    @DisplayName("createProject | Should throw ResourceNotFoundException when Course not found")
    void testCreateProject_Failure_CourseNotFound() {

        when(courseRepository.findById(1L)).thenReturn(Optional.empty());
        ResourceNotFoundException exception = assertThrows(
            ResourceNotFoundException.class,
            () -> projectService.createProject(1L, createDTO)
        );
        
        assertEquals("Course not found with id: 1", exception.getMessage());

        verify(courseRepository, times(1)).findById(1L);
        verify(projectRepository, never()).findByNameAndCourse(anyString(), any());
        verify(projectRepository, never()).save(any());
    }

    @Test
    @DisplayName("createProject | Should throw DuplicateResourceException when Project name exists in Course")
    void testCreateProject_Failure_DuplicateName() {

        when(courseRepository.findById(1L)).thenReturn(Optional.of(existingCourse));
        when(projectRepository.findByNameAndCourse("Test Project", existingCourse)).thenReturn(Optional.of(savedProject));

        DuplicateResourceException exception = assertThrows(
            DuplicateResourceException.class,
            () -> projectService.createProject(1L, createDTO)
        );
        
        assertEquals("A Project with the name 'Test Project' already exists in this course.", exception.getMessage());
        
        verify(courseRepository, times(1)).findById(1L);
        verify(projectRepository, times(1)).findByNameAndCourse("Test Project", existingCourse);
        verify(projectRepository, never()).save(any());
    }

    // Tests for getMyProjects

    @Test
    @DisplayName("getMyProjects | Should return filtered projects and tasks for the student")
    void testGetMyProjects_Success() {
        
        Long studentId = 5L;
        
        student.setId(studentId);

        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));

        Sprint sprint = new Sprint(1, "Sprint Goal", LocalDate.now(), LocalDate.now().plusDays(10), savedProject);
        sprint.setId(50L);

        // Student's own task (Should appear)
        TeamMember memberJohn = new TeamMember();
        memberJohn.setStudent(student);
        
        Task taskJohn = new Task("Task Title John", sprint);
        taskJohn.setId(101L);
        taskJohn.setDescription("Description John");
        taskJohn.setTeamMember(memberJohn);
        taskJohn.setStatus(com.group7.eduscrum_awards.model.enums.TaskStatus.TODO);

        // The other task assigned to a different student (Should not appear)
        Student otherStudent = new Student("Jane", "jane@test.com", "pass");
        otherStudent.setId(99L);
        
        TeamMember memberJane = new TeamMember();
        memberJane.setStudent(otherStudent);

        Task taskJane = new Task("Task Title Jane", sprint);
        taskJane.setId(102L);
        taskJane.setDescription("Description Jane");
        taskJane.setTeamMember(memberJane);
        taskJane.setStatus(com.group7.eduscrum_awards.model.enums.TaskStatus.DOING);

        sprint.setTasks(new HashSet<>(Set.of(taskJohn, taskJane)));
        savedProject.setSprints(new HashSet<>(Set.of(sprint)));

        when(projectRepository.findProjectsByStudentId(studentId)).thenReturn(List.of(savedProject));

        List<StudentProjectDTO> result = projectService.getMyProjects(studentId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Project", result.get(0).getName());
        assertEquals(1, result.get(0).getSprints().size());

        List<StudentTaskDTO> studentTasks = result.get(0).getSprints().get(0).getTasks();
        
        assertEquals(1, studentTasks.size(), "Deve conter apenas as tarefas atribuídas ao aluno");

        assertEquals(101L, studentTasks.get(0).getId());
        assertEquals("Description John", studentTasks.get(0).getDescription());
        
        verify(projectRepository, times(1)).findProjectsByStudentId(studentId);
    }

    @Test
    @DisplayName("getMyProjects | Should throw exception if student not found")
    void testGetMyProjects_StudentNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> projectService.getMyProjects(99L));
        
        verify(projectRepository, never()).findProjectsByStudentId(any());
    }

    @Test
    @DisplayName("getProjectsSummary | Should return summary list")
    void testGetProjectsSummary() {

        Long courseId = 10L;
        com.group7.eduscrum_awards.dto.teacher.ProjectSummaryDTO summary = 
            new com.group7.eduscrum_awards.dto.teacher.ProjectSummaryDTO(1L, "Proj 1", LocalDate.now(), LocalDate.now(), 5L);
        
        when(projectRepository.findProjectsWithTeamCount(courseId)).thenReturn(List.of(summary));

        List<com.group7.eduscrum_awards.dto.teacher.ProjectSummaryDTO> result = projectService.getProjectsSummary(courseId);

        assertEquals(1, result.size());
        assertEquals(5L, result.get(0).getNumberOfTeams());
        verify(projectRepository).findProjectsWithTeamCount(courseId);
    }

    @Test
    @DisplayName("getStudentDashboard | Should return detailed project data with task assignee name")
    void testGetStudentDashboard() {
        Long studentId = 1L;
        
        Student student = new Student("Test Student", "student@test.com", "pass");
        student.setId(studentId);
        
        Project project = new Project("Capstone Project", "Description", existingCourse, LocalDate.now(), LocalDate.now().plusMonths(1));
        project.setId(10L);
        
        Team team = new Team("Alpha Team", 1, project);
        
        TeamMember membership = new TeamMember();
        membership.setId(50L); // TeamMember ID
        membership.setStudent(student);
        membership.setTeam(team);
        membership.setTeamRole(TeamRole.SCRUM_MASTER);

        Task task = new Task("Setup DB", null);
        task.setId(101L);
        task.setStatus(TaskStatus.DOING);
        task.setTeamMember(membership);

        Sprint sprint = new Sprint();
        sprint.setId(100L);
        sprint.setTasks(new java.util.HashSet<>(java.util.Set.of(task))); 
        project.addSprint(sprint);
        
        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(teamMemberRepository.findByStudentId(studentId)).thenReturn(List.of(membership));

        var result = projectService.getStudentDashboard(studentId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Capstone Project", result.get(0).getProjectName());
        
        assertEquals(1, result.get(0).getSprints().size());
        var dashboardTask = result.get(0).getSprints().get(0).getTasks().get(0);
        
        assertEquals(101L, dashboardTask.getId());
        
        // CORREÇÃO: Agora validamos que devolve o studentId (1L) e não o membershipId (50L)
        assertEquals(studentId, dashboardTask.getAssignedUserId()); 
        
        assertEquals("Test Student", dashboardTask.getAssignedMemberName());
    }

    @Test
    @DisplayName("getProjectsByTeacher | Should return list")
    void testGetProjectsByTeacher() {
        Long teacherId = 1L;
        ProjectSummaryDTO summary = new ProjectSummaryDTO(1L, "P1", LocalDate.now(), LocalDate.now(), 2L);
        
        when(projectRepository.findProjectsByTeacherId(teacherId)).thenReturn(List.of(summary));

        List<ProjectSummaryDTO> result = projectService.getProjectsByTeacher(teacherId);
        
        assertEquals(1, result.size());
        verify(projectRepository).findProjectsByTeacherId(teacherId);
    }

    @Test
    @DisplayName("countProjectsInCourse | Should return count")
    void testCountProjectsInCourse() {
        Long courseId = 10L;
        when(projectRepository.countByCourseId(courseId)).thenReturn(5L);

        long count = projectService.countProjectsInCourse(courseId);
        assertEquals(5L, count);
    }

    @Test
    @DisplayName("getProjectDetails | Should assemble full dashboard DTO")
    void testGetProjectDetails() {
        Long projectId = 10L;
        
        Project project = new Project();
        project.setId(projectId);
        project.setName("Details Project");
        
        Sprint sprint = new Sprint();
        sprint.setSprintNumber(1);
        project.addSprint(sprint);

        when(projectRepository.findProjectWithSprintsAndTasks(projectId)).thenReturn(Optional.of(project));
        
        Team team = new Team("Team A", 1, project);
        when(teamRepository.findByProjectId(projectId)).thenReturn(List.of(team));

        TeacherProjectDetailsDTO result = projectService.getProjectDetails(projectId);

        assertNotNull(result);
        assertEquals("Details Project", result.getName());
        assertEquals(1, result.getTeams().size());
        assertEquals(1, result.getSprints().size());
        
        verify(projectRepository).findProjectWithSprintsAndTasks(projectId);
        verify(teamRepository).findByProjectId(projectId);
    }

    @Test
    @DisplayName("getProjectById | Should return DTO when found")
    void testGetProjectById() {
        Long projectId = 10L;
        Project project = new Project("Single Project", "Desc", existingCourse, LocalDate.now(), LocalDate.now());
        project.setId(projectId);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        ProjectDTO result = projectService.getProjectById(projectId);

        assertNotNull(result);
        assertEquals("Single Project", result.getName());
    }

    @Test
    @DisplayName("isTeacherAllowedInProject | Should return true if teacher teaches the course")
    void testIsTeacherAllowedInProject() {
        Long projectId = 10L;
        Long teacherId = 5L;

        Teacher teacher = new Teacher();
        teacher.setId(teacherId);

        Course course = new Course("Course A");
        course.getTeachers().add(teacher);

        Project project = new Project();
        project.setCourse(course);

        when(projectRepository.findById(projectId)).thenReturn(Optional.of(project));

        boolean allowed = projectService.isTeacherAllowedInProject(projectId, teacherId);

        assertEquals(true, allowed);
    }

    @Test
    @DisplayName("getStudentDashboardWithStats | Should return aggregated statistics and projects")
    void testGetStudentDashboardWithStats() {

        Long studentId = 1L;
        Long degreeId = 10L;
        
        Degree degree = new Degree();
        degree.setId(degreeId);
        
        Student student = new Student("John", "john@test.com", "pass");
        student.setId(studentId);
        student.setDegree(degree);

        when(userRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(teamMemberRepository.findByStudentId(studentId)).thenReturn(List.of()); 

        when(awardAssignmentRepository.calculateTotalScore(studentId)).thenReturn(365L);
        when(awardAssignmentRepository.countByStudentId(studentId)).thenReturn(5);
        when(awardAssignmentRepository.countByStudentAndType(studentId, AwardType.MANUAL)).thenReturn(3);
        when(awardAssignmentRepository.countByStudentAndType(studentId, AwardType.AUTOMATIC)).thenReturn(2);
        
        when(teamMemberRepository.countTasksByStudentTeamsAndStatus(studentId, TaskStatus.DONE)).thenReturn(24L);
        
        when(teamMemberRepository.countAllTasksByStudentTeams(studentId)).thenReturn(30L);
        when(userRepository.calculateStudentRankInDegree(365L, degreeId)).thenReturn(3);
        when(userRepository.countByDegreeIdAndRole(degreeId, Role.STUDENT)).thenReturn(45L);

        StudentDashboardDTO result = projectService.getStudentDashboardWithStats(studentId);

        assertNotNull(result);
        assertEquals(365L, result.getTotalScore());
        assertEquals(24L, result.getTasksCompleted());
        assertEquals(30L, result.getTasksTotal());
        assertEquals(3, result.getRanking());
        
        verify(teamMemberRepository).countTasksByStudentTeamsAndStatus(studentId, TaskStatus.DONE);
    }

    @Test
    @DisplayName("getProjectCourseAndTeamCount | Should return course name and team count")
    void testGetProjectCourseAndTeamCount() {

        Long projectId = 1L;
        ProjectCourseTeamsDTO expectedDTO = new ProjectCourseTeamsDTO("Software Engineering", 5L);

        when(projectRepository.findCourseNameAndTeamCountByProjectId(projectId))
                .thenReturn(Optional.of(expectedDTO));

        ProjectCourseTeamsDTO result = projectService.getProjectCourseAndTeamCount(projectId);

        assertNotNull(result);
        assertEquals("Software Engineering", result.getCourseName());
        assertEquals(5L, result.getNumberOfTeams());
        
        verify(projectRepository).findCourseNameAndTeamCountByProjectId(projectId);
    }

    @Test
    @DisplayName("getProjectCourseAndTeamCount | Should throw exception when project not found")
    void testGetProjectCourseAndTeamCount_NotFound() {
        Long projectId = 99L;
        when(projectRepository.findCourseNameAndTeamCountByProjectId(projectId))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            projectService.getProjectCourseAndTeamCount(projectId)
        );
    }
}