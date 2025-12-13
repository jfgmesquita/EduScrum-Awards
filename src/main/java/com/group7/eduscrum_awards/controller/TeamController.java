package com.group7.eduscrum_awards.controller;

import com.group7.eduscrum_awards.dto.DeveloperDTO;
import com.group7.eduscrum_awards.dto.TeamCreateDTO;
import com.group7.eduscrum_awards.dto.TeamDTO;
import com.group7.eduscrum_awards.dto.TeamMemberCreateDTO;
import com.group7.eduscrum_awards.dto.TeamMemberViewDTO;
import com.group7.eduscrum_awards.service.TeamService;
import jakarta.validation.Valid;

import java.security.Principal;
import java.util.List;

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
     * Accessible via: POST /api/v1/projects/{projectId}/teams
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

    /**
     * Endpoint to assign an existing Student to a Team with a specific role
     * This endpoint is intended to be called by a Teacher.
     * Accessible via: POST /api/v1/teams/{teamId}/members
     *
     * @param teamId The ID of the Team.
     * @param createDTO The JSON body containing the studentId and teamRole.
     * @return A ResponseEntity containing the updated TeamDTO and HTTP status 200.
     */
    @PostMapping("/teams/{teamId}/members")
    public ResponseEntity<TeamDTO> addMemberToTeam(@PathVariable Long teamId,
            @Valid @RequestBody TeamMemberCreateDTO createDTO) {
        
        TeamDTO updatedTeam = teamService.addMemberToTeam(teamId, createDTO);
        // Return the updated team and a 200 OK status
        return new ResponseEntity<>(updatedTeam, HttpStatus.OK);
    }

    /**
     * Endpoint to retrieve all Teams associated with a specific Project.
     * Accessible via: GET /api/v1/projects/{projectId}/teams
     *
     * @param projectId The ID of the Project.
     * @return A ResponseEntity containing a list of TeamDTOs and HTTP status 200.
     */
    @GetMapping("/projects/{projectId}/teams")
    public ResponseEntity<List<TeamDTO>> getTeamsByProject(@PathVariable Long projectId) {
        return ResponseEntity.ok(teamService.getTeamsByProject(projectId));
    }

    /**
     * Endpoint to retrieve all Teams associated with a specific Course.
     * Accessible via: GET /api/v1/courses/{courseId}/teams
     *
     * @param courseId The ID of the Course.
     * @return A ResponseEntity containing a list of TeamDTOs and HTTP status 200.
     */
    @GetMapping("/courses/{courseId}/teams")
    public ResponseEntity<List<TeamDTO>> getTeamsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(teamService.getTeamsByCourse(courseId));
    }

    /**
     * Endpoint to retrieve all Developers (TeamMembers with DEVELOPER role)
     * from the current user's team, given a context of a Sprint or a Task.
     * Accessible via: GET /api/v1/teams/developers?sprintId={sprintId}&taskId={taskId}
     *
     * @param sprintId Optional Sprint ID as a request parameter.
     * @param taskId Optional Task ID as a request parameter.
     * @param principal The security principal of the currently logged-in user.
     * @return A ResponseEntity containing a list of DeveloperDTOs and HTTP status 200.
     */
    @GetMapping("/teams/developers")
    public ResponseEntity<List<DeveloperDTO>> getDevelopers(
            @RequestParam(required = false) Long sprintId,
            @RequestParam(required = false) Long taskId,
            Principal principal) {
        
        List<DeveloperDTO> developers = teamService.getDevelopersByContext(sprintId, taskId, principal.getName());
        return ResponseEntity.ok(developers);
    }

    /**
     * Endpoint to retrieve all members of a specific team.
     * Accessible via: GET /api/v1/teams/{teamId}/members
     *
     * @param teamId The ID of the team.
     * @return A ResponseEntity containing a list of TeamMemberViewDTOs and HTTP status 200.
     */
    @GetMapping("/teams/{teamId}/members")
    public ResponseEntity<List<TeamMemberViewDTO>> getTeamMembers(@PathVariable Long teamId) {
        return ResponseEntity.ok(teamService.getTeamMembers(teamId));
    }
}