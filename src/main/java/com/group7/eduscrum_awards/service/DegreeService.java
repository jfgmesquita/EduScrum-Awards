package com.group7.eduscrum_awards.service;

import com.group7.eduscrum_awards.dto.DegreeCreateDTO;
import com.group7.eduscrum_awards.dto.DegreeDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;

/**
 * Service Interface (Contract) that provide the business logic for creating
 * and managing {@link DegreeDTO} instances.
 * 
 * It defines what operations can be performed related to Degrees, but not how they are implemented.
 */
public interface DegreeService {

    /**
     * Register a new degree.
     * 
     * Creates and persists a new degree described by the provided
     * {@link DegreeCreateDTO} and returns the created {@link DegreeDTO}.
     *
     * @param degreeCreateDTO the data required to create the degree; must not be null
     * @return the created {@link DegreeDTO} containing the generated id and stored values
     * @throws DuplicateResourceException if a degree with the same name already exists or if the input is invalid
     */
    DegreeDTO registerDegree(DegreeCreateDTO degreeCreateDTO);

    /**
     * Assigns an existing Student to an existing Degree.
     *
     * @param degreeId The ID of the Degree.
     * @param studentId The ID of the Student to assign.
     * @return A DTO of the updated Degree.
     * @throws ResourceNotFoundException if either ID is not found or user is not a Student.
     * @throws DuplicateResourceException if the student is already assigned to this degree.
     */
    DegreeDTO addStudentToDegree(Long degreeId, Long studentId);
}
