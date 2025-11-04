package com.group7.eduscrum_awards.service;

import com.group7.eduscrum_awards.dto.ProjectCreateDTO;
import com.group7.eduscrum_awards.dto.ProjectDTO;

/**
 * Service Interface (Contract) for Project operations.
 * Defines business logic methods for managing projects.
 */
public interface ProjectService {

    /**
     * Creates a new Project and associates it with a specific Course.
     * This operation is intended to be called by a Teacher.
     *
     * @param courseId The ID of the Course this project will belong to.
     * @param projectCreateDTO The DTO containing the data for the new Project.
     * @return The DTO of the newly created Project.
     * @throws ResourceNotFoundException if the courseId does not exist.
     * @throws DuplicateResourceException if a Project with the same name already exists in that Course.
     */
    ProjectDTO createProject(Long courseId, ProjectCreateDTO projectCreateDTO);
}