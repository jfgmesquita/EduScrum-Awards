package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.RankingItemDTO;
import com.group7.eduscrum_awards.service.UserService;
import com.group7.eduscrum_awards.service.JwtService;

import com.group7.eduscrum_awards.service.RankingService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
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

/** Unit tests for RankingController. */
@WebMvcTest(RankingController.class)
class RankingControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private RankingService rankingService;
    @MockitoBean private UserService userService;
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
    @DisplayName("getStudentRankings | Should return list")
    @WithMockUser
    void testGetStudentRankings() throws Exception {
        Long degreeId = 1L;
        RankingItemDTO item = new RankingItemDTO();

        when(rankingService.getStudentRanking(degreeId)).thenReturn(List.of(item));

        mockMvc.perform(get("/api/v1/degrees/{degreeId}/rankings", degreeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("getTeamRankings | Should return list")
    @WithMockUser
    void testGetTeamRankings() throws Exception {
        Long courseId = 2L;
        RankingItemDTO item = new RankingItemDTO();

        when(rankingService.getTeamRanking(courseId)).thenReturn(List.of(item));

        mockMvc.perform(get("/api/v1/courses/{courseId}/rankings/teams", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("getStudentDashboardRankings | Should return complex ranking DTO")
    @WithMockUser(username = "student@test.com")
    void testGetStudentDashboardRankings() throws Exception {
        Long studentId = 5L;
        String email = "student@test.com";

        com.group7.eduscrum_awards.dto.UserDTO mockUser = new com.group7.eduscrum_awards.dto.UserDTO();
        mockUser.setId(studentId);
        mockUser.setEmail(email);
        
        when(userService.getUserByEmail(email)).thenReturn(mockUser);

        com.group7.eduscrum_awards.dto.rankings.StudentDashboardRankingDTO dashboardDTO = 
            new com.group7.eduscrum_awards.dto.rankings.StudentDashboardRankingDTO();
        
        dashboardDTO.setIndividualRankings(List.of());
        dashboardDTO.setTeamRankingsByCourse(List.of());

        when(rankingService.getStudentDashboardRankings(studentId)).thenReturn(dashboardDTO);

        mockMvc.perform(get("/api/v1/students/{studentId}/rankings", studentId))
                .andExpect(status().isOk());
    }
}