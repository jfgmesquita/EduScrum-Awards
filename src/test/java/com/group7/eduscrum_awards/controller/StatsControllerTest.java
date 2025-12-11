package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.stats.*;
import com.group7.eduscrum_awards.service.JwtService;
import com.group7.eduscrum_awards.service.StatsService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Unit tests for StatsController. */
@WebMvcTest(StatsController.class)
class StatsControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockitoBean private StatsService statsService;
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
    @WithMockUser
    void getGlobalStats_ShouldReturnData() throws Exception {
        when(statsService.getGlobalStats()).thenReturn(new GlobalStatsDTO(1, 2, 3, 4));

        mockMvc.perform(get("/api/v1/stats/global"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalDegrees").value(1))
                .andExpect(jsonPath("$.totalCourses").value(2));
    }

    @Test
    @WithMockUser
    void getDegreeStats_ShouldReturnData() throws Exception {
        when(statsService.getDegreeStats(1L)).thenReturn(new DegreeStatsDTO(5, 10, 2));

        mockMvc.perform(get("/api/v1/stats/degrees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.coursesCount").value(5));
    }
}