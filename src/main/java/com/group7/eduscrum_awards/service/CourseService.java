package com.group7.eduscrum_awards.service;

import com.group7.eduscrum_awards.dto.CourseCreateDTO;
import com.group7.eduscrum_awards.dto.CourseDTO;

/**
 * Service Interface (Contract) for Course operations.
 * Defines business logic methods for managing courses.
 */
public interface CourseService {

    /**
     * Registers a new Course in the system.
     *
     * @param courseCreateDTO The DTO containing the data for the new Course.
     * @return The DTO of the newly created Course.
     * @throws DuplicateResourceException if a Course with the same name already exists.
     */
    CourseDTO registerCourse(CourseCreateDTO courseCreateDTO);

    // Future methods can be added here, e.g.:
    // List<CourseDTO> getAllCourses();
    // CourseDTO addTeacherToCourse(Long courseId, Long teacherId);
}