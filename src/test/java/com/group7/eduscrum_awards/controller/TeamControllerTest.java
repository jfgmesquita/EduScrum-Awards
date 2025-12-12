package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.TeamDTO;
import com.group7.eduscrum_awards.model.Team;
import com.group7.eduscrum_awards.service.JwtService;
import com.group7.eduscrum_awards.service.TeamService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Unit tests for TeamController. */
@WebMvcTest(TeamController.class)
class TeamControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private TeamService teamService;
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
    @DisplayName("getTeamsByProject | Should return list of teams")
    @WithMockUser(roles = "TEACHER")
    void testGetTeamsByProject() throws Exception {

        Long projectId = 10L;
        Team team = new Team();
        team.setId(1L);
        team.setName("Alpha Team");

        TeamDTO teamDTO = new TeamDTO(team); 

        when(teamService.getTeamsByProject(projectId)).thenReturn(List.of(teamDTO));

        mockMvc.perform(get("/api/v1/projects/" + projectId + "/teams")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Alpha Team"));
    }
}