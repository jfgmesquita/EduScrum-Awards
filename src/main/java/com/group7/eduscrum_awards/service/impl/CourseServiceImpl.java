package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.CourseCreateDTO;
import com.group7.eduscrum_awards.dto.CourseDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Degree;
import com.group7.eduscrum_awards.repository.CourseRepository;
import com.group7.eduscrum_awards.repository.DegreeRepository;
import com.group7.eduscrum_awards.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final DegreeRepository degreeRepository;

    @Autowired
    public CourseServiceImpl(CourseRepository courseRepository, DegreeRepository degreeRepository) {
        this.courseRepository = courseRepository;
        this.degreeRepository = degreeRepository;
    }

    /**
     * Registers a new course for a specific degree.
     */
    @Override
    @Transactional
    public CourseDTO registerCourseForDegree(Long degreeId, CourseCreateDTO courseCreateDTO) {
        
        // Find the Degree (the parent entity)
        Degree degree = degreeRepository.findById(degreeId)
            .orElseThrow(() -> new ResourceNotFoundException("Degree not found with id: " + degreeId));

        // Check for duplicates WITHIN that Degree
        // (using the repository method that checks by name and degree)
        courseRepository.findByNameAndDegree(courseCreateDTO.getName(), degree)
            .ifPresent(c -> {
                throw new DuplicateResourceException("A Course with the name '" + courseCreateDTO.getName() + "' already exists for this Degree.");
            });

        // Create the Course entity
        Course newCourse = new Course(courseCreateDTO.getName());
        
        // Link the entities (using the Degree helper method)
        degree.addCourse(newCourse);

        // Save the new Course entity
        Course savedCourse = courseRepository.save(newCourse);
        return new CourseDTO(savedCourse);
    }
}