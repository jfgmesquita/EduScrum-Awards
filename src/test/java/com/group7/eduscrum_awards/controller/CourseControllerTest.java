package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.CourseDTO;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.service.CourseService;
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

@WebMvcTest(CourseController.class)
class CourseControllerTest {
    
@Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;

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
    @DisplayName("Should return list of courses when authenticated")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllCourses_Success() throws Exception {

        Course course1 = new Course("Software Quality");
        course1.setId(1L);
        Course course2 = new Course("Project Management");
        course2.setId(2L);

        List<CourseDTO> courses = Arrays.asList(new CourseDTO(course1), new CourseDTO(course2));

        when(courseService.getAllCourses()).thenReturn(courses);

        mockMvc.perform(get("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expect 200 OK
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Software Quality"))
                .andExpect(jsonPath("$[1].name").value("Project Management"));
    }
    
    @Test
    @DisplayName("Should return empty list if no courses found")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void testGetAllCourses_Empty() throws Exception {

        when(courseService.getAllCourses()).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/courses")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));
    }
}
