package com.group7.eduscrum_awards.service;

import com.group7.eduscrum_awards.dto.stats.*;

/**
 * Service interface for retrieving various statistics related to the EduScrum Awards system.
 * Provides methods to obtain global, degree, course, and teacher statistics.
 */
public interface StatsService {
    /**
     * Retrieves global statistics for the system.
     *
     * @return a {@link GlobalStatsDTO} containing global statistics data.
     */
    GlobalStatsDTO getGlobalStats();

    /**
     * Retrieves statistics for a specific degree.
     *
     * @param degreeId the ID of the degree for which statistics are requested.
     * @return a {@link DegreeStatsDTO} containing statistics for the specified degree.
     * @throws IllegalArgumentException if the degreeId is null or invalid.
     */
    DegreeStatsDTO getDegreeStats(Long degreeId);

    /**
     * Retrieves statistics for a specific course.
     *
     * @param courseId the ID of the course for which statistics are requested.
     * @return a {@link CourseStatsDTO} containing statistics for the specified course.
     * @throws IllegalArgumentException if the courseId is null or invalid.
     */
    CourseStatsDTO getCourseStats(Long courseId);

    /**
     * Retrieves statistics for a specific teacher.
     *
     * @param teacherId the ID of the teacher for which statistics are requested.
     * @return a {@link TeacherStatsDTO} containing statistics for the specified teacher.
     * @throws IllegalArgumentException if the teacherId is null or invalid.
     */
    TeacherStatsDTO getTeacherStats(Long teacherId);
}