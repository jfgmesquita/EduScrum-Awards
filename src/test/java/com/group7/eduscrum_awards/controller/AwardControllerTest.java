package com.group7.eduscrum_awards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.AwardAssignmentRequestDTO;
import com.group7.eduscrum_awards.dto.AwardCreateDTO;
import com.group7.eduscrum_awards.dto.AwardDTO;
import com.group7.eduscrum_awards.dto.StudentAwardDTO;
import com.group7.eduscrum_awards.service.AwardService;
import com.group7.eduscrum_awards.service.JwtService;
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
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Unit tests for {@link AwardController}. */
@WebMvcTest(AwardController.class)
class AwardControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private AwardService awardService;
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
    @DisplayName("createCustomAward | Should return 201 Created")
    @WithMockUser(roles = "TEACHER")
    void testCreateCustomAward() throws Exception {
        Long courseId = 1L;
        
        AwardCreateDTO createDTO = new AwardCreateDTO();
        createDTO.setName("Best Coder");
        createDTO.setDescription("For excellent code");
        createDTO.setPoints(5); 

        AwardDTO responseDTO = new AwardDTO();
        responseDTO.setId(10L);
        responseDTO.setName("Best Coder");
        responseDTO.setPoints(5);

        when(awardService.createCustomAward(eq(courseId), any(AwardCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/courses/{courseId}/awards", courseId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Best Coder"));
    }

    @Test
    @DisplayName("getAvailableAwards | Should return list of awards")
    @WithMockUser
    void testGetAvailableAwards() throws Exception {
        Long courseId = 1L;
        AwardDTO award = new AwardDTO();
        award.setName("Global Award");
        
        when(awardService.getAvailableAwards(courseId)).thenReturn(List.of(award));

        mockMvc.perform(get("/api/v1/courses/{courseId}/awards", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("assignAward | Should return 200 OK")
    @WithMockUser(roles = "TEACHER")
    void testAssignAward() throws Exception {
        Long awardId = 5L;
        
        AwardAssignmentRequestDTO requestDTO = new AwardAssignmentRequestDTO();
        requestDTO.setProjectId(100L); 
        requestDTO.setStudentId(10L); 

        doNothing().when(awardService).assignAward(eq(awardId), any(AwardAssignmentRequestDTO.class));

        mockMvc.perform(post("/api/v1/awards/{awardId}/assign", awardId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("getStudentAwards | Should return student portfolio")
    @WithMockUser
    void testGetStudentAwards() throws Exception {
        Long studentId = 2L;
        StudentAwardDTO dto = new StudentAwardDTO();

        when(awardService.getStudentAwards(studentId)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/v1/students/{studentId}/awards", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }
}