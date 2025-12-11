package com.group7.eduscrum_awards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;
import com.group7.eduscrum_awards.dto.studentdashboard.StudentProjectDTO;
import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.service.JwtService;
import com.group7.eduscrum_awards.service.ProjectService;
import jakarta.servlet.FilterChain;
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
 * Unit tests for the ProjectController.
 * These tests focus on the controller layer, mocking the service layer to isolate controller behavior.
 */
@WebMvcTest(ProjectController.class)
class ProjectControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private ProjectService projectService;
    @MockitoBean private JwtService jwtService;
    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {
        // Bypass JWT
        doAnswer(inv -> {
            ((FilterChain) inv.getArgument(2)).doFilter(inv.getArgument(0), inv.getArgument(1));
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
    @DisplayName("getStudentProjects | Should return list of projects for dashboard")
    @WithMockUser(roles = "STUDENT")
    void testGetStudentProjects() throws Exception {

        Long studentId = 5L;
        
        Project dummyProject = new Project();
        dummyProject.setId(20L);
        dummyProject.setName("Student Project");
        
        StudentProjectDTO dto = new StudentProjectDTO(dummyProject, studentId);
        
        when(projectService.getMyProjects(studentId)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/students/{studentId}/projects", studentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(20L))
                .andExpect(jsonPath("$[0].name").value("Student Project"));
        
        verify(projectService, times(1)).getMyProjects(studentId);
    }
}