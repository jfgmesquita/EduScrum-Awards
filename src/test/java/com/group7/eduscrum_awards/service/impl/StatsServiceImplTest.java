package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.stats.*;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.repository.CourseRepository;
import com.group7.eduscrum_awards.repository.DegreeRepository;
import com.group7.eduscrum_awards.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Unit tests for StatsServiceImpl. */
@ExtendWith(MockitoExtension.class)
class StatsServiceImplTest {

    @Mock private UserRepository userRepository;
    @Mock private DegreeRepository degreeRepository;
    @Mock private CourseRepository courseRepository;

    @InjectMocks
    private StatsServiceImpl statsService;

    @Test
    @DisplayName("getGlobalStats | Should return correct counts")
    void testGetGlobalStats() {
        when(degreeRepository.count()).thenReturn(5L);
        when(courseRepository.count()).thenReturn(10L);
        when(userRepository.countByRole(Role.STUDENT)).thenReturn(100L);
        when(userRepository.countByRole(Role.TEACHER)).thenReturn(20L);

        GlobalStatsDTO result = statsService.getGlobalStats();

        assertEquals(5, result.getTotalDegrees());
        assertEquals(10, result.getTotalCourses());
        assertEquals(100, result.getTotalStudents());
        assertEquals(20, result.getTotalTeachers());
    }

    @Test
    @DisplayName("getDegreeStats | Should return counts for specific degree")
    void testGetDegreeStats() {
        Long degreeId = 1L;
        
        when(degreeRepository.existsById(degreeId)).thenReturn(true);
        
        when(courseRepository.countByDegreeId(degreeId)).thenReturn(3L);
        when(userRepository.countByDegreeIdAndRole(degreeId, Role.STUDENT)).thenReturn(50L);
        when(userRepository.countDistinctTeachersByDegreeId(degreeId)).thenReturn(5L);

        DegreeStatsDTO result = statsService.getDegreeStats(degreeId);

        assertEquals(3, result.getCoursesCount());
        assertEquals(50, result.getStudentsCount());
        assertEquals(5, result.getTeachersCount());
    }

    @Test
    @DisplayName("getCourseStats | Should return counts for specific course")
    void testGetCourseStats() {
        Long courseId = 10L;

        when(courseRepository.existsById(courseId)).thenReturn(true);

        when(courseRepository.countStudentsByCourseId(courseId)).thenReturn(30L);
        when(courseRepository.countTeachersByCourseId(courseId)).thenReturn(2L);

        CourseStatsDTO result = statsService.getCourseStats(courseId);

        assertEquals(30, result.getStudentsCount());
        assertEquals(2, result.getTeachersCount());
    }

    @Test
    @DisplayName("getTeacherStats | Should return course count for teacher")
    void testGetTeacherStats() {
        Long teacherId = 99L;

        when(userRepository.existsById(teacherId)).thenReturn(true);

        when(courseRepository.countCoursesByTeacherId(teacherId)).thenReturn(4L);

        TeacherStatsDTO result = statsService.getTeacherStats(teacherId);

        assertEquals(4, result.getCoursesCount());
    }

    @Test
    @DisplayName("getDegreeStats | Should throw exception when degree not found")
    void testGetDegreeStats_NotFound() {
        Long degreeId = 999L;
        when(degreeRepository.existsById(degreeId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> 
            statsService.getDegreeStats(degreeId));
        
        verify(courseRepository, never()).countByDegreeId(any());
    }

    @Test
    @DisplayName("getCourseStats | Should throw exception when course not found")
    void testGetCourseStats_NotFound() {
        Long courseId = 999L;
        when(courseRepository.existsById(courseId)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> 
            statsService.getCourseStats(courseId));
    }
}