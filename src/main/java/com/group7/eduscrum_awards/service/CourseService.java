package com.group7.eduscrum_awards.service;

import com.group7.eduscrum_awards.dto.CourseCreateDTO;
import com.group7.eduscrum_awards.dto.CourseDTO;

/**
 * Service Interface (Contract) for Course operations.
 * Defines business logic methods for managing courses.
 */
public interface CourseService {

    /**
     * Registers a new Course and associates it with a specific Degree.
     *
     * @param degreeId The ID of the Degree this course will belong to.
     * @param courseCreateDTO The DTO containing the data for the new Course.
     * @return The DTO of the newly created Course.
     * @throws ResourceNotFoundException if the degreeId does not exist.
     * @throws DuplicateResourceException if a Course with the same name already exists within that Degree.
     */
    CourseDTO registerCourseForDegree(Long degreeId, CourseCreateDTO courseCreateDTO);

    // Future methods can be added here, e.g.:
    // List<CourseDTO> getAllCourses();
    // CourseDTO addTeacherToCourse(Long courseId, Long teacherId);
}