package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.TaskCreateDTO;
import com.group7.eduscrum_awards.dto.TaskDTO;
import com.group7.eduscrum_awards.dto.TaskAssignDTO;
import com.group7.eduscrum_awards.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing Tasks.
 * Exposes endpoints for task creation by Product Owners.
 */
@RestController
@RequestMapping("/api/v1") 
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    /**
     * Endpoint to create a new Task within a specific Sprint.
     * This endpoint is intended to be called by a Student (Product Owner).
     * Accessible via: POST /api/v1/sprints/{sprintId}/tasks
     *
     * @param sprintId The ID of the parent Sprint (from the URL path).
     * @param taskCreateDTO The task data (description) from the request body.
     * @return A ResponseEntity containing the created TaskDTO and HTTP status 201.
     */
    @PostMapping("/sprints/{sprintId}/tasks")
    public ResponseEntity<TaskDTO> createTask(@PathVariable Long sprintId,
            @Valid @RequestBody TaskCreateDTO taskCreateDTO) {
        
        TaskDTO newTask = taskService.createTask(sprintId, taskCreateDTO);
        // Return the new task and a 201 Created status
        return new ResponseEntity<>(newTask, HttpStatus.CREATED);
    }

    /**
     * Endpoint to assign a Task to a TeamMember (Developer).
     * This endpoint is intended to be called by a Student (Product Owner).
     * Accessible via: PATCH /api/v1/tasks/{taskId}/assign
     *
     * @param taskId The ID of the Task to be updated.
     * @param assignDTO The JSON body containing the teamMemberId.
     * @return A ResponseEntity containing the updated TaskDTO and HTTP status 200.
     */
    @PatchMapping("/tasks/{taskId}/assign")
    public ResponseEntity<TaskDTO> assignTask(@PathVariable Long taskId,
            @Valid @RequestBody TaskAssignDTO assignDTO) {
        
        TaskDTO updatedTask = taskService.assignTask(taskId, assignDTO);
        // Return 200 OK with the updated task
        return new ResponseEntity<>(updatedTask, HttpStatus.OK);
    }
}