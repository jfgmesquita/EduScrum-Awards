package com.group7.eduscrum_awards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.DegreeCreateDTO;
import com.group7.eduscrum_awards.dto.DegreeDTO;
import com.group7.eduscrum_awards.dto.DegreeUpdateDTO;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Unit tests for DegreeController. */
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

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("getDegreeById | Should return degree")
    @WithMockUser
    void testGetDegreeById() throws Exception {
        Long id = 1L;
        DegreeDTO dto = new DegreeDTO(); 
        dto.setId(id); dto.setName("CS");

        when(degreeService.getDegreeById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/degrees/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id));
    }

    @Test
    @DisplayName("registerDegree | Should create and return 201")
    @WithMockUser(roles = "ADMIN")
    void testRegisterDegree() throws Exception {
        DegreeCreateDTO createDTO = new DegreeCreateDTO();
        createDTO.setName("New Degree");

        DegreeDTO responseDTO = new DegreeDTO();
        responseDTO.setId(1L);
        responseDTO.setName("New Degree");

        when(degreeService.registerDegree(any(DegreeCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/degrees")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("New Degree"));
    }

    @Test
    @DisplayName("addStudentToDegree | Should return 200 OK")
    @WithMockUser(roles = "ADMIN")
    void testAddStudentToDegree() throws Exception {
        Long degreeId = 1L;
        Long studentId = 10L;

        DegreeDTO responseDTO = new DegreeDTO();
        responseDTO.setId(degreeId);

        when(degreeService.addStudentToDegree(degreeId, studentId)).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/degrees/{degreeId}/students/{studentId}", degreeId, studentId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(degreeId));
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

    @Test
    @DisplayName("updateDegree | Should update degree name")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testUpdateDegree_Success() throws Exception {
 
        Long id = 1L;
        DegreeUpdateDTO updateDTO = new DegreeUpdateDTO();
        updateDTO.setName("New Name");

        DegreeDTO responseDTO = new DegreeDTO();
        responseDTO.setId(id);
        responseDTO.setName("New Name");

        when(degreeService.updateDegree(eq(id), any(DegreeUpdateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/degrees/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"));
    }
}