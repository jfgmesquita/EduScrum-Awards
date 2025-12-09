package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.DegreeDTO;
import com.group7.eduscrum_awards.model.Degree;
import com.group7.eduscrum_awards.service.DegreeService;
import com.group7.eduscrum_awards.service.JwtService;
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

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DegreeController.class)
class DegreeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DegreeService degreeService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void setUp() throws Exception {
        // Mock the JWT filter to bypass security during tests logic
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());
    }

    @Test
    @DisplayName("Should return list of degrees when authenticated")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllDegrees_Success() throws Exception {

        Degree degree1 = new Degree("Computer Science");
        degree1.setId(1L);
        Degree degree2 = new Degree("Civil Engineering");
        degree2.setId(2L);

        List<DegreeDTO> degrees = Arrays.asList(new DegreeDTO(degree1), new DegreeDTO(degree2));

        when(degreeService.getAllDegrees()).thenReturn(degrees);

        mockMvc.perform(get("/api/v1/degrees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Computer Science"))
                .andExpect(jsonPath("$[1].name").value("Civil Engineering"));
    }
    
    @Test
    @DisplayName("Should return empty list if no degrees found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllDegrees_Empty() throws Exception {

        when(degreeService.getAllDegrees()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/degrees")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
}