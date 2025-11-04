package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.TeamCreateDTO;
import com.group7.eduscrum_awards.dto.TeamDTO;
import com.group7.eduscrum_awards.service.TeamService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for managing Teams.
 * Exposes endpoints for team creation by Teachers.
 */
@RestController
@RequestMapping("/api/v1")
public class TeamController {

    private final TeamService teamService;

    @Autowired
    public TeamController(TeamService teamService) {
        this.teamService = teamService;
    }

    /**
     * Endpoint to create a new Team within a specific Project.
     * This endpoint is intended to be called by a Teacher.
     * Accessible via: POST http://localhost:8080/api/v1/projects/{projectId}/teams
     *
     * @param projectId The ID of the parent Project (from the URL path).
     * @param teamCreateDTO The team data from the request body.
     * @return A ResponseEntity containing the created TeamDTO and HTTP status 201.
     */
    @PostMapping("/projects/{projectId}/teams")
    public ResponseEntity<TeamDTO> createTeam(@PathVariable Long projectId,
            @Valid @RequestBody TeamCreateDTO teamCreateDTO) {
        
        TeamDTO newTeam = teamService.createTeam(projectId, teamCreateDTO);
        // Return the new team and a 201 Created status
        return new ResponseEntity<>(newTeam, HttpStatus.CREATED);
    }
}