package com.group7.eduscrum_awards.service;

import com.group7.eduscrum_awards.dto.TaskAssignDTO;
import com.group7.eduscrum_awards.dto.TaskCreateDTO;
import com.group7.eduscrum_awards.dto.TaskDTO;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;

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

    /**
     * Assigns an existing Task to a specific TeamMember (Developer).
     * This operation validates that the logged-in user is the
     * Product Owner for the project this task belongs to.
     *
     * @param taskId The ID of the Task to be updated.
     * @param assignDTO The DTO containing the ID of the TeamMember to assign.
     * @return The DTO of the updated Task.
     * @throws ResourceNotFoundException if the taskId or teamMemberId does not exist.
     * @throws AccessDeniedException if the user is not the PO for this project.
     * @throws IllegalArgumentException if the developer is not on a team for this project.
     */
    TaskDTO assignTask(Long taskId, TaskAssignDTO assignDTO);
}