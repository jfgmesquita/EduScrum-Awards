package com.group7.eduscrum_awards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.TaskAssignDTO;
import com.group7.eduscrum_awards.dto.TaskCreateDTO;
import com.group7.eduscrum_awards.dto.TaskDTO;
import com.group7.eduscrum_awards.service.JwtService;
import com.group7.eduscrum_awards.service.TaskService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Unit tests for TaskController. */
@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private TaskService taskService;

    @MockitoBean private JwtService jwtService;

    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {
        doAnswer(inv -> {
            ((FilterChain) inv.getArgument(2)).doFilter(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    @DisplayName("createTask | Should return 201 Created")
    @WithMockUser(roles = "STUDENT")
    void testCreateTask() throws Exception {
        Long sprintId = 10L;
        TaskCreateDTO createDTO = new TaskCreateDTO();
        createDTO.setDescription("Implement Login");

        TaskDTO responseDTO = new TaskDTO();
        responseDTO.setId(1L);
        responseDTO.setDescription("Implement Login");

        when(taskService.createTask(eq(sprintId), any(TaskCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/sprints/{sprintId}/tasks", sprintId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("assignTask | Should return 200 OK")
    @WithMockUser(roles = "STUDENT")
    void testAssignTask() throws Exception {
        Long taskId = 1L;
        
        TaskAssignDTO assignDTO = new TaskAssignDTO();
        assignDTO.setTeamMemberId(10L); 

        TaskDTO responseDTO = new TaskDTO();
        responseDTO.setId(taskId);

        when(taskService.assignTask(eq(taskId), any(TaskAssignDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(patch("/api/v1/tasks/{taskId}/assign", taskId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(assignDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(taskId));
    }
}