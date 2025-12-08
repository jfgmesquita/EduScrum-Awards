package com.group7.eduscrum_awards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.TeacherRegistrationDTO;
import com.group7.eduscrum_awards.dto.UserCreateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.service.CourseService;
import com.group7.eduscrum_awards.service.JwtService;
import com.group7.eduscrum_awards.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for UserController.
 */
@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    // Test data
    private TeacherRegistrationDTO registrationDTO;
    private UserDTO createdUserDTO;

    @BeforeEach
    void setUp() throws Exception {
        // Mock the JWT filter to bypass security during tests
        doAnswer(invocation -> {
            ServletRequest request = invocation.getArgument(0);
            ServletResponse response = invocation.getArgument(1);
            FilterChain chain = invocation.getArgument(2);
            chain.doFilter(request, response);
            return null;
        }).when(jwtAuthenticationFilter).doFilter(any(), any(), any());

        // Initialize test data
        registrationDTO = new TeacherRegistrationDTO();
        registrationDTO.setName("Prof Test");
        registrationDTO.setEmail("prof@test.com");
        registrationDTO.setPassword("pass1234");
        registrationDTO.setCourseIdToAssign(10L);
        
        createdUserDTO = new UserDTO();
        createdUserDTO.setId(1L);
        createdUserDTO.setName("Prof Test");
        createdUserDTO.setEmail("prof@test.com");
        createdUserDTO.setRole(Role.TEACHER);
    }

    @Test
    @DisplayName("Should register teacher and assign course successfully")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRegisterTeacherWithCourse_Success() throws Exception {

        when(userService.registerUser(any(UserCreateDTO.class))).thenReturn(createdUserDTO);
        
        mockMvc.perform(post("/api/v1/users/teachers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isCreated()) // 201
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.role").value("TEACHER"));

        verify(userService, times(1)).registerUser(any(UserCreateDTO.class));
        verify(courseService, times(1)).addTeacherToCourse(10L, 1L);
    }

    @Test
    @DisplayName("Should rollback transaction when course not found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRegisterTeacherWithCourse_Rollback() throws Exception {

        when(userService.registerUser(any(UserCreateDTO.class))).thenReturn(createdUserDTO);
        
        doThrow(new ResourceNotFoundException("Course not found"))
            .when(courseService).addTeacherToCourse(10L, 1L);

        mockMvc.perform(post("/api/v1/users/teachers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registrationDTO)))
                .andExpect(status().isNotFound()); // 404

        verify(userService, times(1)).registerUser(any(UserCreateDTO.class));
        verify(courseService, times(1)).addTeacherToCourse(10L, 1L);
    }
}