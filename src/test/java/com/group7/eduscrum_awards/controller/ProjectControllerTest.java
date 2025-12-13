package com.group7.eduscrum_awards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.dto.dashboard.StudentDashboardProjectDTO;
import com.group7.eduscrum_awards.dto.dashboard.TeacherProjectDetailsDTO;
import com.group7.eduscrum_awards.dto.studentdashboard.StudentProjectDTO;
import com.group7.eduscrum_awards.dto.teacher.ProjectSummaryDTO;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Project;
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
    @DisplayName("getStudentProjects | Should return list when User is Owner")
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void testGetStudentProjects_Success() throws Exception {
        Long studentId = 5L;

        UserDTO mockUser = new UserDTO();
        mockUser.setId(studentId);
        mockUser.setName("Student Name");

        when(userService.getUserByEmail("student@test.com")).thenReturn(mockUser);

        Project dummyProject = new Project();
        dummyProject.setId(20L);
        dummyProject.setName("Student Project");
        StudentProjectDTO dto = new StudentProjectDTO(dummyProject, studentId);

        when(projectService.getMyProjects(studentId)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/students/{studentId}/projects", studentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(20L));
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
    @DisplayName("getStudentProjects | Should return 404 when student not found (even if authorized)")
    @WithMockUser(username = "student@test.com", roles = "STUDENT")
    void testGetStudentProjects_NotFound() throws Exception {
        Long studentId = 5L;

        UserDTO mockUser = new UserDTO();
        mockUser.setId(studentId);
        when(userService.getUserByEmail("student@test.com")).thenReturn(mockUser);

        when(projectService.getMyProjects(studentId))
            .thenThrow(new ResourceNotFoundException("Student projects not found"));

        mockMvc.perform(get("/api/v1/students/{studentId}/projects", studentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("getStudentDashboard | Should return 200 OK with data")
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

        mockMvc.perform(get("/api/v1/students/{studentId}/dashboard", studentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].projectName").value("Dashboard Project"))
                .andExpect(jsonPath("$[0].myRole").value("DEVELOPER"));
    }

    @Test
    @DisplayName("getStudentDashboard | Should return 403 Forbidden on IDOR attempt")
    @WithMockUser(username = "hacker@test.com", roles = "STUDENT")
    void testGetStudentDashboard_Forbidden() throws Exception {
        Long targetId = 5L;
        Long hackerId = 99L;

        UserDTO hackerUser = new UserDTO();
        hackerUser.setId(hackerId);
        when(userService.getUserByEmail("hacker@test.com")).thenReturn(hackerUser);

        mockMvc.perform(get("/api/v1/students/{studentId}/dashboard", targetId)
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
}