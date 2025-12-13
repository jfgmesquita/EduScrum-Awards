package com.group7.eduscrum_awards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.SprintCreateDTO;
import com.group7.eduscrum_awards.dto.SprintDTO;
import com.group7.eduscrum_awards.service.JwtService;
import com.group7.eduscrum_awards.service.SprintService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Unit tests for SprintController. */
@WebMvcTest(SprintController.class)
class SprintControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private SprintService sprintService;
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
    @DisplayName("createSprint | Should return 201 Created")
    @WithMockUser(roles = "STUDENT")
    void testCreateSprint() throws Exception {
        Long projectId = 1L;
        
        SprintCreateDTO createDTO = new SprintCreateDTO();
        createDTO.setFinalGoal("MVP");
        createDTO.setSprintNumber(1);
        createDTO.setStartDate(LocalDate.now());
        createDTO.setEndDate(LocalDate.now().plusWeeks(2));

        SprintDTO responseDTO = new SprintDTO();
        responseDTO.setId(10L);
        responseDTO.setFinalGoal("MVP");

        when(sprintService.createSprint(eq(projectId), any(SprintCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/projects/{projectId}/sprints", projectId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L));
    }
}