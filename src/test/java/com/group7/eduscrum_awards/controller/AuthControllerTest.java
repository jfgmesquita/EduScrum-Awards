package com.group7.eduscrum_awards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.LoginRequestDTO;
import com.group7.eduscrum_awards.dto.LoginResponseDTO;
import com.group7.eduscrum_awards.service.AuthService;
import com.group7.eduscrum_awards.service.JwtService;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Unit tests for AuthController. */
@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private AuthService authService;

    @MockitoBean private JwtService jwtService;

    @MockitoBean private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {
        // Mock the JWT filter to bypass security during tests logic
        doAnswer(inv -> {
            ((FilterChain) inv.getArgument(2)).doFilter(inv.getArgument(0), inv.getArgument(1));
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    @DisplayName("login | Should return token and user details on success")
    void testLogin() throws Exception {

        LoginRequestDTO loginReq = new LoginRequestDTO();
        loginReq.setEmail("teacher@test.com");
        loginReq.setPassword("password123");

        LoginResponseDTO loginRes = new LoginResponseDTO(
            "ey.jwt.fake.token", 
            10L, 
            "teacher@test.com", 
            "TEACHER"
        );

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(loginRes);

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("ey.jwt.fake.token"))
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.email").value("teacher@test.com"))
                .andExpect(jsonPath("$.role").value("TEACHER"));
    }
}