package com.group7.eduscrum_awards.service;

import java.util.List;

import com.group7.eduscrum_awards.dto.CourseCreateDTO;
import com.group7.eduscrum_awards.dto.CourseDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;

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

    /**
     * Assigns an existing Teacher to an existing Course.
     * This method is transactional to ensure the relationship is saved.
     *
     * @param courseId The ID of the course.
     * @param teacherId The ID of the teacher to add.
     * @return A DTO of the updated Course.
     * @throws ResourceNotFoundException if the course or teacher is not found.
     */
    CourseDTO addTeacherToCourse(Long courseId, Long teacherId);

    /**
     * Enrolls an existing Student in an existing Course.
     *
     * @param courseId The ID of the course.
     * @param studentId The ID of the student to enroll.
     * @return A DTO of the updated Course.
     * @throws ResourceNotFoundException if either ID is not found or user is not a Student.
     * @throws DuplicateResourceException if the student is already enrolled in this course.
     */
    CourseDTO addStudentToCourse(Long courseId, Long studentId);


    /**
     * Retrieves all courses in the system.
     *
     * @return A list of {@link CourseDTO} representing all courses.
     */
    List<CourseDTO> getAllCourses();

    // Future methods can be added here, e.g.:
    // CourseDTO addTeacherToCourse(Long courseId, Long teacherId);
}