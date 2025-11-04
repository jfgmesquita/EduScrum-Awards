package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.CourseCreateDTO;
import com.group7.eduscrum_awards.dto.CourseDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.repository.CourseRepository;
import com.group7.eduscrum_awards.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing Course entities.
 * Implements the {@link CourseService} contract.
 */
@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    /**
     * Registers a new course, performing a uniqueness check on the name.
     *
     * @param courseCreateDTO the DTO containing the course data
     * @return a {@link CourseDTO} representing the created course
     * @throws DuplicateResourceException if the course name is already in use
     */
    @Override
    @Transactional
    public CourseDTO registerCourse(CourseCreateDTO courseCreateDTO) {
        
        // Check for duplicate name
        courseRepository.findByName(courseCreateDTO.getName())
            .ifPresent(course -> {
                throw new DuplicateResourceException("A Course with the name '" + courseCreateDTO.getName() + "' already exists.");
            });

        // Map DTO to Entity (course is created with an empty list of teachers)
        Course newCourse = new Course(courseCreateDTO.getName());

        // Save the new Course
        Course savedCourse = courseRepository.save(newCourse);

        // Map Entity to Response DTO
        return new CourseDTO(savedCourse);
    }
}