package com.group7.eduscrum_awards.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.group7.eduscrum_awards.config.JwtAuthenticationFilter;
import com.group7.eduscrum_awards.dto.CourseCreateDTO;
import com.group7.eduscrum_awards.dto.CourseDTO;
import com.group7.eduscrum_awards.dto.CourseUpdateDTO;
import com.group7.eduscrum_awards.dto.UserDTO;
import com.group7.eduscrum_awards.dto.teacher.ProjectSummaryDTO;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.service.CourseService;
import com.group7.eduscrum_awards.service.JwtService;
import com.group7.eduscrum_awards.service.ProjectService;

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

import java.time.LocalDate;
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

@WebMvcTest(CourseController.class)
class CourseControllerTest {
    
@Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private ProjectService projectService;

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
    @DisplayName("getCourseById | Should return course")
    @WithMockUser
    void testGetCourseById() throws Exception {
        Long id = 10L;
        CourseDTO dto = new CourseDTO(); dto.setId(id); dto.setName("Java");

        when(courseService.getCourseById(id)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/courses/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Java"));
    }

    @Test
    @DisplayName("getCoursesByStudent | Should return list")
    @WithMockUser
    void testGetCoursesByStudent() throws Exception {
        Long studentId = 5L;
        when(courseService.getCoursesByStudent(studentId)).thenReturn(List.of(new CourseDTO()));

        mockMvc.perform(get("/api/v1/students/{studentId}/courses", studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
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

    @Test
    @DisplayName("updateCourse | Should update course details")
    @WithMockUser(roles = "ADMIN")
    void testUpdateCourse() throws Exception {
        Long id = 1L;
        CourseUpdateDTO updateDTO = new CourseUpdateDTO();
        updateDTO.setName("New Course Name");

        CourseDTO responseDTO = new CourseDTO();
        responseDTO.setId(id);
        responseDTO.setName("New Course Name");

        when(courseService.updateCourse(eq(id), any(CourseUpdateDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(put("/api/v1/courses/{id}", id)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Course Name"));
    }

    @Test
    @DisplayName("getCoursesByDegree | Should return filtered list")
    @WithMockUser
    void testGetCoursesByDegree() throws Exception {
        Long degreeId = 5L;
        when(courseService.getCoursesByDegree(degreeId)).thenReturn(List.of(new CourseDTO()));

        mockMvc.perform(get("/api/v1/degrees/{id}/courses", degreeId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));
    }

    @Test
    @DisplayName("getStudentsInCourse | Should return list of students")
    @WithMockUser
    void testGetStudentsInCourse() throws Exception {
        Long courseId = 10L;
        UserDTO student = new UserDTO();
        student.setName("Student 1");
        
        when(courseService.getStudentsInCourse(courseId)).thenReturn(List.of(student));

        mockMvc.perform(get("/api/v1/courses/{id}/students", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Student 1"));
    }

    @Test
    @DisplayName("getProjectsSummary | Should return project list with team counts")
    @WithMockUser(roles = "TEACHER")
    void testGetProjectsSummary() throws Exception {
        Long courseId = 1L;
        ProjectSummaryDTO summary = new ProjectSummaryDTO(10L, "Proj A", LocalDate.now(), LocalDate.now(), 3L);

        when(projectService.getProjectsSummary(courseId)).thenReturn(List.of(summary));

        mockMvc.perform(get("/api/v1/courses/{id}/projects/summary", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].numberOfTeams").value(3));
    }

    @Test
    @DisplayName("registerCourseForDegree | Should create a new course and return 201 Created")
    @WithMockUser(roles = "ADMIN")
    void testRegisterCourseForDegree() throws Exception {
        Long degreeId = 1L;
        
        CourseCreateDTO createDTO = new CourseCreateDTO();
        createDTO.setName("Advanced Software Engineering");

        CourseDTO createdCourse = new CourseDTO();
        createdCourse.setId(10L);
        createdCourse.setName("Advanced Software Engineering");

        when(courseService.registerCourseForDegree(eq(degreeId), any(CourseCreateDTO.class)))
                .thenReturn(createdCourse);

        mockMvc.perform(post("/api/v1/degrees/{degreeId}/courses", degreeId)
                .with(csrf()) // Required for POST requests in security tests
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10L))
                .andExpect(jsonPath("$.name").value("Advanced Software Engineering"));
    }

    @Test
    @DisplayName("addTeacherToCourse | Should assign teacher and return 200 OK")
    @WithMockUser(roles = "ADMIN")
    void testAddTeacherToCourse() throws Exception {
        Long courseId = 10L;
        Long teacherId = 5L;

        CourseDTO updatedCourse = new CourseDTO();
        updatedCourse.setId(courseId);
        updatedCourse.setName("Course with Teacher");

        when(courseService.addTeacherToCourse(courseId, teacherId)).thenReturn(updatedCourse);

        mockMvc.perform(post("/api/v1/courses/{courseId}/teachers/{teacherId}", courseId, teacherId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId))
                .andExpect(jsonPath("$.name").value("Course with Teacher"));
    }

    @Test
    @DisplayName("addStudentToCourse | Should enroll student and return 200 OK")
    @WithMockUser(roles = {"TEACHER", "ADMIN"})
    void testAddStudentToCourse() throws Exception {
        Long courseId = 10L;
        Long studentId = 20L;

        CourseDTO updatedCourse = new CourseDTO();
        updatedCourse.setId(courseId);

        when(courseService.addStudentToCourse(courseId, studentId)).thenReturn(updatedCourse);

        mockMvc.perform(post("/api/v1/courses/{courseId}/students/{studentId}", courseId, studentId)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(courseId));
    }

    @Test
    @DisplayName("getCoursesByTeacher | Should return list of courses for specific teacher")
    @WithMockUser
    void testGetCoursesByTeacher() throws Exception {
        Long teacherId = 5L;

        CourseDTO course1 = new CourseDTO();
        course1.setName("Teacher Course 1");
        
        when(courseService.getCoursesByTeacher(teacherId)).thenReturn(List.of(course1));

        mockMvc.perform(get("/api/v1/teachers/{teacherId}/courses", teacherId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Teacher Course 1"));
    }
}
