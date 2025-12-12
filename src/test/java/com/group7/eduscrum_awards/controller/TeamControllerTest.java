package com.group7.eduscrum_awards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.TeamCreateDTO;
import com.group7.eduscrum_awards.dto.TeamDTO;
import com.group7.eduscrum_awards.dto.TeamMemberCreateDTO;
import com.group7.eduscrum_awards.model.Team;
import com.group7.eduscrum_awards.model.enums.TeamRole;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Unit tests for TeamController. */
@WebMvcTest(TeamController.class)
class TeamControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

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
    @DisplayName("createTeam | Should return 201 Created")
    @WithMockUser(roles = "TEACHER")
    void testCreateTeam() throws Exception {
        Long projectId = 1L;
        
        TeamCreateDTO createDTO = new TeamCreateDTO();
        createDTO.setName("New Team");
        createDTO.setGroupNumber(1); 

        TeamDTO responseDTO = new TeamDTO();
        responseDTO.setId(10L);
        responseDTO.setName("New Team");
        responseDTO.setGroupNumber(1);

        when(teamService.createTeam(eq(projectId), any(TeamCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/projects/{projectId}/teams", projectId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("New Team"));
    }

    @Test
    @DisplayName("addMemberToTeam | Should return 200 OK")
    @WithMockUser(roles = "TEACHER")
    void testAddMemberToTeam() throws Exception {
        Long teamId = 10L;
        
        TeamMemberCreateDTO memberDTO = new TeamMemberCreateDTO();
        memberDTO.setStudentId(5L);
        memberDTO.setTeamRole(TeamRole.DEVELOPER);

        TeamDTO responseDTO = new TeamDTO();
        responseDTO.setId(teamId);

        when(teamService.addMemberToTeam(eq(teamId), any(TeamMemberCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/teams/{teamId}/members", teamId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(memberDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(teamId));
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