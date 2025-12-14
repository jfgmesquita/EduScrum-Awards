package com.group7.eduscrum_awards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.ProjectCourseTeamsDTO;
import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.dto.dashboard.StudentDashboardDTO;
import com.group7.eduscrum_awards.dto.dashboard.StudentDashboardProjectDTO;
import com.group7.eduscrum_awards.dto.dashboard.TeacherProjectDetailsDTO;
import com.group7.eduscrum_awards.dto.teacher.ProjectSummaryDTO;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.service.JwtService;
import com.group7.eduscrum_awards.service.ProjectService;
import com.group7.eduscrum_awards.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for ProjectController.
 * Focuses on security and correct responses for project-related endpoints.
 */
@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProjectService projectService;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    @DisplayName("createProject | Should return 201 Created")
    @WithMockUser(roles = "TEACHER")
    void testCreateProject() throws Exception {
        Long courseId = 1L;
        ProjectCreateDTO createDTO = new ProjectCreateDTO();
        createDTO.setName("New Project");
        createDTO.setDescription("Desc");
        createDTO.setStartDate(LocalDate.now());
        createDTO.setEndDate(LocalDate.now().plusMonths(1));

        ProjectDTO responseDTO = new ProjectDTO();
        responseDTO.setId(10L);
        responseDTO.setName("New Project");

        when(projectService.createProject(eq(courseId), any(ProjectCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/courses/{courseId}/projects", courseId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("New Project"));
    }

    @Test
    @DisplayName("getStudentProjects | Should return 403 Forbidden when accessing another student data")
    @WithMockUser(username = "hacker@test.com", roles = "STUDENT")
    void testGetStudentProjects_Forbidden() throws Exception {
        Long targetStudentId = 5L;

        UserDTO hackerUser = new UserDTO();
        hackerUser.setId(99L); 
        
        when(userService.getUserByEmail("hacker@test.com")).thenReturn(hackerUser);

        mockMvc.perform(get("/api/v1/students/{studentId}/projects", targetStudentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        
        verify(projectService, never()).getMyProjects(any());
    }

    @Test
    @DisplayName("getStudentProjects | Should return 200 OK with data")
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void testGetStudentDashboard() throws Exception {
        Long studentId = 5L;

        UserDTO mockUser = new UserDTO();
        mockUser.setId(studentId);
        when(userService.getUserByEmail("student@test.com")).thenReturn(mockUser);

        StudentDashboardProjectDTO dashboardDTO = new StudentDashboardProjectDTO();
        dashboardDTO.setProjectName("Dashboard Project");
        dashboardDTO.setMyRole("DEVELOPER");
        
        when(projectService.getStudentDashboard(studentId)).thenReturn(List.of(dashboardDTO));

        mockMvc.perform(get("/api/v1/students/{studentId}/projects", studentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectName").value("Dashboard Project"))
                .andExpect(jsonPath("$[0].myRole").value("DEVELOPER"));
    }

    @Test
    @DisplayName("getStudentProjects | Should return 403 Forbidden on IDOR attempt")
    @WithMockUser(username = "hacker@test.com", roles = "STUDENT")
    void testGetStudentDashboard_Forbidden() throws Exception {
        Long targetId = 5L;
        Long hackerId = 99L;

        UserDTO hackerUser = new UserDTO();
        hackerUser.setId(hackerId);
        when(userService.getUserByEmail("hacker@test.com")).thenReturn(hackerUser);

        mockMvc.perform(get("/api/v1/students/{studentId}/projects", targetId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("getProjectsByTeacher | Should return list when User matches Teacher ID")
    @WithMockUser(username = "teacher@test.com", roles = "TEACHER")
    void testGetProjectsByTeacher_Success() throws Exception {
        Long teacherId = 10L;
        UserDTO mockUser = new UserDTO();
        mockUser.setId(teacherId);
        mockUser.setEmail("teacher@test.com");
        
        when(userService.getUserByEmail("teacher@test.com")).thenReturn(mockUser);

        ProjectSummaryDTO summary = new ProjectSummaryDTO(1L, "Proj A", LocalDate.now(), LocalDate.now(), 5L);
        when(projectService.getProjectsByTeacher(teacherId)).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/teachers/{teacherId}/projects", teacherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Proj A"));
    }

    @Test
    @DisplayName("getProjectsByTeacher | Should return 403 Forbidden on IDOR")
    @WithMockUser(username = "hacker@test.com", roles = "TEACHER")
    void testGetProjectsByTeacher_Forbidden() throws Exception {
        Long targetId = 10L;
        UserDTO hacker = new UserDTO();
        hacker.setId(99L);
        when(userService.getUserByEmail("hacker@test.com")).thenReturn(hacker);

        mockMvc.perform(get("/api/v1/teachers/{teacherId}/projects", targetId))
                .andExpect(status().isForbidden());
        
        verify(projectService, never()).getProjectsByTeacher(any());
    }

    @Test
    @DisplayName("getProjectsByCourse | Should return summary list")
    @WithMockUser(roles = "TEACHER")
    void testGetProjectsByCourse() throws Exception {
        Long courseId = 5L;
        ProjectSummaryDTO summary = new ProjectSummaryDTO(1L, "Course Proj", LocalDate.now(), LocalDate.now(), 2L);
        
        when(projectService.getProjectsSummary(courseId)).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/courses/{courseId}/projects", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("getProjectCount | Should return count")
    @WithMockUser(roles = "TEACHER")
    void testGetProjectCount() throws Exception {
        Long courseId = 5L;
        when(projectService.countProjectsInCourse(courseId)).thenReturn(10L);

        mockMvc.perform(get("/api/v1/courses/{courseId}/projects/count", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(10));
    }

    @Test
    @DisplayName("getProjectDetails | Should return complex dashboard DTO")
    @WithMockUser(roles = "TEACHER")
    void testGetProjectDetails() throws Exception {
        Long projectId = 50L;
        TeacherProjectDetailsDTO details = new TeacherProjectDetailsDTO();
        details.setId(projectId);
        details.setName("Full Dashboard");
        
        when(projectService.getProjectDetails(projectId)).thenReturn(details);

        mockMvc.perform(get("/api/v1/projects/{projectId}/details", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Full Dashboard"));
    }

    @Test
    @DisplayName("getProjectById | Should return 200 OK for authorized Teacher")
    @WithMockUser(username = "teacher@test.com", roles = "TEACHER")
    void testGetProjectById_Success() throws Exception {
        Long projectId = 10L;
        Long teacherId = 1L;

        // Mock User
        UserDTO user = new UserDTO();
        user.setId(teacherId);
        user.setRole(Role.TEACHER);
        when(userService.getUserByEmail("teacher@test.com")).thenReturn(user);

        // Mock Security Check (Allowed)
        when(projectService.isTeacherAllowedInProject(projectId, teacherId)).thenReturn(true);

        // Mock Data Retrieval
        ProjectDTO dto = new ProjectDTO();
        dto.setId(projectId);
        dto.setName("My Project");
        when(projectService.getProjectById(projectId)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/projects/{id}", projectId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("My Project"));
    }

    @Test
    @DisplayName("getProjectById | Should return 403 Forbidden on IDOR")
    @WithMockUser(username = "hacker@test.com", roles = "TEACHER")
    void testGetProjectById_Forbidden() throws Exception {
        Long projectId = 10L;
        Long teacherId = 99L;

        UserDTO user = new UserDTO();
        user.setId(teacherId);
        user.setRole(Role.TEACHER);
        when(userService.getUserByEmail("hacker@test.com")).thenReturn(user);

        // Mock Security Check (Denied)
        when(projectService.isTeacherAllowedInProject(projectId, teacherId)).thenReturn(false);

        mockMvc.perform(get("/api/v1/projects/{id}", projectId))
                .andExpect(status().isForbidden());
                
        verify(projectService, never()).getProjectById(any());
    }

    @Test
    @DisplayName("getStudentStats | Should return 200 OK when user is authorized")
    @WithMockUser(username = "john@test.com", roles = "STUDENT")
    void testGetStudentStats_Success() throws Exception {
        Long studentId = 1L;
        
        // Mock User Service for IDOR check
        UserDTO userDTO = new UserDTO();
        userDTO.setId(studentId);
        userDTO.setEmail("john@test.com");
        when(userService.getUserByEmail("john@test.com")).thenReturn(userDTO);

        // Mock Service response
        StudentDashboardDTO dashboardDTO = new StudentDashboardDTO();
        dashboardDTO.setTotalScore(100L);
        when(projectService.getStudentDashboardWithStats(studentId)).thenReturn(dashboardDTO);

        mockMvc.perform(get("/api/v1/students/{studentId}/dashboard", studentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalScore").value(100));
    }

    @Test
    @DisplayName("getStudentStats | Should return 403 Forbidden when IDOR detected")
    @WithMockUser(username = "hacker@test.com", roles = "STUDENT")
    void testGetStudentStats_Forbidden_IDOR() throws Exception {
        Long targetStudentId = 1L;
        Long hackerId = 99L;

        // Mock User Service returning a different ID than the requested one
        UserDTO hackerUser = new UserDTO();
        hackerUser.setId(hackerId);
        hackerUser.setEmail("hacker@test.com");
        when(userService.getUserByEmail("hacker@test.com")).thenReturn(hackerUser);

        mockMvc.perform(get("/api/v1/students/{studentId}/dashboard", targetStudentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
        
        verify(projectService, never()).getStudentDashboardWithStats(anyLong());
    }

    @Test
    @DisplayName("getProjectCourseAndTeamCount | Should return 200 OK and DTO when project exists")
    @WithMockUser(username = "teacher@test.com", roles = "TEACHER")
    void testGetProjectCourseAndTeamCount_Success() throws Exception {
        Long projectId = 1L;
        ProjectCourseTeamsDTO expectedDTO = new ProjectCourseTeamsDTO("Software Engineering", 5L);

        when(projectService.getProjectCourseAndTeamCount(projectId)).thenReturn(expectedDTO);

        mockMvc.perform(get("/api/v1/projects/{projectId}/course-teams", projectId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseName").value("Software Engineering"))
                .andExpect(jsonPath("$.numberOfTeams").value(5));
        
        verify(projectService).getProjectCourseAndTeamCount(projectId);
    }

    @Test
    @DisplayName("getProjectCourseAndTeamCount | Should return 404 Not Found when project does not exist")
    @WithMockUser(username = "teacher@test.com", roles = "TEACHER")
    void testGetProjectCourseAndTeamCount_NotFound() throws Exception {

        Long projectId = 99L;

        when(projectService.getProjectCourseAndTeamCount(projectId))
                .thenThrow(new ResourceNotFoundException("Project not found"));

        mockMvc.perform(get("/api/v1/projects/{projectId}/course-teams", projectId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        
        verify(projectService).getProjectCourseAndTeamCount(projectId);
    }
}