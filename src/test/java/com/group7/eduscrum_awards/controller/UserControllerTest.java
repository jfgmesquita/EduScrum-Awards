package com.group7.eduscrum_awards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.PasswordUpdateDTO;
import com.group7.eduscrum_awards.dto.StudentUpdateDTO;
import com.group7.eduscrum_awards.dto.TeacherRegistrationDTO;
import com.group7.eduscrum_awards.dto.UserCreateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.service.CourseService;
import com.group7.eduscrum_awards.service.JwtService;
import com.group7.eduscrum_awards.service.UserService;

import java.util.Arrays;
import java.util.List;

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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Unit tests for UserController. */
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
    @DisplayName("registerUser | Should register a generic user and return 201 Created")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRegisterUser_Success() throws Exception {
        UserCreateDTO createDTO = new UserCreateDTO();
        createDTO.setName("New Student");
        createDTO.setEmail("newstudent@test.com");
        createDTO.setPassword("password123");
        createDTO.setRole(Role.STUDENT);

        UserDTO responseDTO = new UserDTO();
        responseDTO.setId(5L);
        responseDTO.setName("New Student");
        responseDTO.setEmail("newstudent@test.com");
        responseDTO.setRole(Role.STUDENT);

        when(userService.registerUser(any(UserCreateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/api/v1/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated()) // Expect 201
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.name").value("New Student"))
                .andExpect(jsonPath("$.role").value("STUDENT"));
        
        verify(userService, times(1)).registerUser(any(UserCreateDTO.class));
    }

    @Test
    @DisplayName("registerUser | Should return 400 Bad Request when input is invalid")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testRegisterUser_Invalid() throws Exception {
        UserCreateDTO invalidDTO = new UserCreateDTO();
        invalidDTO.setName("");

        mockMvc.perform(post("/api/v1/users")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());
        
        verify(userService, never()).registerUser(any());
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

    @Test
    @DisplayName("Should list all students")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllStudents_Success() throws Exception {
        
        UserDTO student1 = new UserDTO();
        student1.setName("Alice");
        student1.setRole(Role.STUDENT);

        List<UserDTO> students = Arrays.asList(student1);
        when(userService.getAllStudents()).thenReturn(students);

        mockMvc.perform(get("/api/v1/users/students")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Alice"));
        
        verify(userService, times(1)).getAllStudents();
    }

    @Test
    @DisplayName("Should list all teachers")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllTeachers_Success() throws Exception {

        UserDTO teacher1 = new UserDTO();
        teacher1.setName("Professor X");
        teacher1.setRole(Role.TEACHER);

        List<UserDTO> teachers = Arrays.asList(teacher1);
        when(userService.getAllTeachers()).thenReturn(teachers);

        mockMvc.perform(get("/api/v1/users/teachers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Professor X"));

        verify(userService, times(1)).getAllTeachers();
    }

    @Test
    @DisplayName("updateStudent | Should update student info")
    @WithMockUser(roles = "ADMIN")
    void testUpdateStudent() throws Exception {
        Long id = 1L;
        StudentUpdateDTO updateDTO = new StudentUpdateDTO();
        updateDTO.setName("Updated Name");

        UserDTO responseDTO = new UserDTO();
        responseDTO.setName("Updated Name");

        when(userService.updateStudent(eq(id), any(StudentUpdateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/users/students/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    @DisplayName("updatePassword | Should return 204 No Content")
    @WithMockUser(roles = "ADMIN")
    void testUpdatePassword() throws Exception {
        Long id = 1L;
        PasswordUpdateDTO passDTO = new PasswordUpdateDTO();
        passDTO.setNewPassword("newSecret123");

        doNothing().when(userService).updatePassword(eq(id), anyString());

        mockMvc.perform(patch("/api/v1/users/{id}/password", id) 
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passDTO)))
                .andExpect(status().isNoContent()); // 204
    }
}