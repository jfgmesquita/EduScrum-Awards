package com.group7.eduscrum_awards.service;

import com.group7.eduscrum_awards.dto.DegreeCreateDTO;
import com.group7.eduscrum_awards.dto.DegreeDTO;

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
     * @throws RuntimeException if a degree with the same name already exists or if the input is invalid
     */
    DegreeDTO registerDegree(DegreeCreateDTO degreeCreateDTO);

    // Additional operations can be added later, for example:
    // List<DegreeDTO> getAllDegrees();
    // Optional<DegreeDTO> getDegreeByName(String name);
    // void deleteDegree(Long id);
}
