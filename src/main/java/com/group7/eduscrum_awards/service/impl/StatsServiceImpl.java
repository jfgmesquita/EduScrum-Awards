package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.stats.*;
import com.group7.eduscrum_awards.model.enums.Role;
import com.group7.eduscrum_awards.repository.CourseRepository;
import com.group7.eduscrum_awards.repository.DegreeRepository;
import com.group7.eduscrum_awards.repository.UserRepository;
import com.group7.eduscrum_awards.service.StatsService;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the {@link StatsService} interface.
 * Provides methods to retrieve various statistics related to users, degrees, and courses.
 */
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final UserRepository userRepository;
    private final DegreeRepository degreeRepository;
    private final CourseRepository courseRepository;

    /** Retrieves global statistics. */
    @Override
    @Transactional(readOnly = true)
    public GlobalStatsDTO getGlobalStats() {
        return new GlobalStatsDTO(
            degreeRepository.count(),
            courseRepository.count(),
            userRepository.countByRole(Role.STUDENT),
            userRepository.countByRole(Role.TEACHER)
        );
    }

    /** Retrieves degree-specific statistics. */
    @Override
    @Transactional(readOnly = true)
    public DegreeStatsDTO getDegreeStats(Long degreeId) {

        if (!degreeRepository.existsById(degreeId)) {
            throw new ResourceNotFoundException("Degree not found with id: " + degreeId);
        }

        return new DegreeStatsDTO(
            courseRepository.countByDegreeId(degreeId),
            userRepository.countByDegreeIdAndRole(degreeId, Role.STUDENT),
            userRepository.countDistinctTeachersByDegreeId(degreeId)
        );
    }

    /** Retrieves course-specific statistics. */
    @Override
    @Transactional(readOnly = true)
    public CourseStatsDTO getCourseStats(Long courseId) {

        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("Course not found with id: " + courseId);
        }

        return new CourseStatsDTO(
            courseRepository.countStudentsByCourseId(courseId),
            courseRepository.countTeachersByCourseId(courseId)
        );
    }

    /** Retrieves teacher-specific statistics. */
    @Override
    @Transactional(readOnly = true)
    public TeacherStatsDTO getTeacherStats(Long teacherId) {

        if (!userRepository.existsById(teacherId)) {
            throw new ResourceNotFoundException("Teacher not found with id: " + teacherId);
        }

        return new TeacherStatsDTO(
            courseRepository.countCoursesByTeacherId(teacherId)
        );
    }
}