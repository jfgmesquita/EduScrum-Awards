package com.group7.eduscrum_awards.service;

import com.group7.eduscrum_awards.dto.TaskCreateDTO;
import com.group7.eduscrum_awards.dto.TaskDTO;

/** Service Interface (Contract) for Task operations. */
public interface TaskService {

    /**
     * Creates a new Task and associates it with a specific Sprint.
     * This operation validates that the logged-in user is the
     * Product Owner for the project this sprint belongs to.
     *
     * @param sprintId The ID of the Sprint this task will belong to.
     * @param taskCreateDTO The DTO containing the data for the new Task.
     * @return The DTO of the newly created Task.
     * @throws ResourceNotFoundException if the sprintId does not exist.
     * @throws AccessDeniedException if the user is not the PO for this project.
     */
    TaskDTO createTask(Long sprintId, TaskCreateDTO taskCreateDTO);
}