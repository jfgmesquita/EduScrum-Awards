package com.group7.eduscrum_awards.service;

import com.group7.eduscrum_awards.dto.SprintCreateDTO;
import com.group7.eduscrum_awards.dto.SprintDTO;

/**
 * Service Interface (Contract) for Sprint operations.
 * Defines business logic methods for managing sprints.
 */
public interface SprintService {

    /**
     * Creates a new Sprint and associates it with a specific Project.
     * This operation is intended to be called by a Product Owner.
     *
     * @param projectId The ID of the Project this sprint will belong to.
     * @param sprintCreateDTO The DTO containing the data for the new Sprint.
     * @return The DTO of the newly created Sprint.
     * @throws ResourceNotFoundException if the projectId does not exist.
     * @throws DuplicateResourceException if a Sprint with the same number already exists in that Project.
     */
    SprintDTO createSprint(Long projectId, SprintCreateDTO sprintCreateDTO);
}