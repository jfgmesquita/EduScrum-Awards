package com.group7.eduscrum_awards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.dto.studentdashboard.StudentProjectDTO;
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
        mockUser.setId(studentId); // ID 5
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
}