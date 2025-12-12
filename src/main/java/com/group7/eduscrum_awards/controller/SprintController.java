package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.SprintCreateDTO;
import com.group7.eduscrum_awards.dto.SprintDTO;
import com.group7.eduscrum_awards.service.SprintService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing Sprints.
 * Exposes endpoints for sprint creation by Product Owners.
 */
@RestController
@RequestMapping("/api/v1")
public class SprintController {

    private final SprintService sprintService;

    @Autowired
    public SprintController(SprintService sprintService) {
        this.sprintService = sprintService;
    }

    /**
     * Endpoint to create a new Sprint within a specific Project.
     * This endpoint is intended to be called by a PO.
     * Accessible via: POST /api/v1/projects/{projectId}/sprints
     *
     * @param projectId The ID of the parent Project (from the URL path).
     * @param sprintCreateDTO The sprint data from the request body.
     * @return A ResponseEntity containing the created SprintDTO and HTTP status 201.
     */
    @PostMapping("/projects/{projectId}/sprints")
    public ResponseEntity<SprintDTO> createSprint(@PathVariable Long projectId,
            @Valid @RequestBody SprintCreateDTO sprintCreateDTO) {

        SprintDTO newSprint = sprintService.createSprint(projectId, sprintCreateDTO);
        // Return the new sprint and a 201 Created status
        return new ResponseEntity<>(newSprint, HttpStatus.CREATED);
    }
}