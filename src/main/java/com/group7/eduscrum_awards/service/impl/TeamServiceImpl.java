package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.TeamCreateDTO;
import com.group7.eduscrum_awards.dto.TeamDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Team;
import com.group7.eduscrum_awards.repository.ProjectRepository;
import com.group7.eduscrum_awards.repository.TeamRepository;
import com.group7.eduscrum_awards.service.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository, ProjectRepository projectRepository) {
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
    }

    /**
     * Creates a new team for a specific project.
     * Validates uniqueness of name and group number within the project.
     */
    @Override
    @Transactional
    public TeamDTO createTeam(Long projectId, TeamCreateDTO teamCreateDTO) {
        
        // Find the parent Project
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        // Check for duplicate group number *within* this project
        teamRepository.findByGroupNumberAndProject(teamCreateDTO.getGroupNumber(), project)
            .ifPresent(t -> {
                throw new DuplicateResourceException("A team with group number " + teamCreateDTO.getGroupNumber() + " already exists in this project.");
            });
        
        // Check for duplicate name *within* this project
        teamRepository.findByNameAndProject(teamCreateDTO.getName(), project)
            .ifPresent(t -> {
                throw new DuplicateResourceException("A team with the name '" + teamCreateDTO.getName() + "' already exists in this project.");
            });

        // Create the new Team entity
        Team newTeam = new Team(
            teamCreateDTO.getName(),
            teamCreateDTO.getGroupNumber(),
            project
        );

        // Save the new team (the "Many" side)
        Team savedTeam = teamRepository.save(newTeam);

        // Return the DTO
        return new TeamDTO(savedTeam);
    }
}