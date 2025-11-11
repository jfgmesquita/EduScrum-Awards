package com.group7.eduscrum_awards.service;

import com.group7.eduscrum_awards.dto.TeamCreateDTO;
import com.group7.eduscrum_awards.dto.TeamDTO;
import com.group7.eduscrum_awards.dto.TeamMemberCreateDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;

/**
 * Service Interface (Contract) for Team operations.
 * Defines business logic methods for managing teams.
 */
public interface TeamService {

    /**
     * Creates a new Team and associates it with a specific Project.
     * This operation is intended to be called by a Teacher.
     *
     * @param projectId The ID of the Project this team will belong to.
     * @param teamCreateDTO The DTO containing the data for the new Team.
     * @return The DTO of the newly created Team.
     * @throws ResourceNotFoundException if the projectId does not exist.
     * @throws DuplicateResourceException if a Team with the same name or group number already exists in that Project.
     */
    TeamDTO createTeam(Long projectId, TeamCreateDTO teamCreateDTO);

    /**
     * Adds a student to a team with a specific role.
     * Enforces the rule that a student can only be on one team per project.
     *
     * @param teamId The ID of the Team.
     * @param createDTO The DTO containing the studentId and their role.
     * @return A DTO of the updated Team.
     */
    TeamDTO addMemberToTeam(Long teamId, TeamMemberCreateDTO createDTO);
}