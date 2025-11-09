package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.SprintCreateDTO;
import com.group7.eduscrum_awards.dto.SprintDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Sprint;
import com.group7.eduscrum_awards.model.Student; 
import com.group7.eduscrum_awards.model.User;    
import com.group7.eduscrum_awards.model.enums.TeamRole; 
import com.group7.eduscrum_awards.repository.ProjectRepository;
import com.group7.eduscrum_awards.repository.SprintRepository;
import com.group7.eduscrum_awards.repository.TeamMemberRepository;
import com.group7.eduscrum_awards.service.SprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException; 
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the SprintService.
 * Handles the logic for creating sprints.
 */
@Service
public class SprintServiceImpl implements SprintService {

    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Autowired
    public SprintServiceImpl(SprintRepository sprintRepository, ProjectRepository projectRepository, TeamMemberRepository teamMemberRepository) {
        this.sprintRepository = sprintRepository;
        this.projectRepository = projectRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    /**
     * Creates a new sprint for a specific project.
     * Validates uniqueness of the sprint number within the project.
     */
    @Override
    @Transactional
    public SprintDTO createSprint(Long projectId, SprintCreateDTO sprintCreateDTO) {
        
        // Find the parent Project
        Project project = projectRepository.findById(projectId)
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + projectId));

        // Verify the logged-in user is the Product Owner of this project
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        boolean isProductOwner = teamMemberRepository.findByStudentAndProject((Student) currentUser, project)
            .map(member -> member.getTeamRole() == TeamRole.PRODUCT_OWNER)
            .orElse(false);
        if (!isProductOwner) {
            throw new AccessDeniedException("You are not the Product Owner for this project.");
        }

        // Check for duplicate sprint number within this project
        sprintRepository.findBySprintNumberAndProject(sprintCreateDTO.getSprintNumber(), project)
            .ifPresent(s -> {
                throw new DuplicateResourceException("A Sprint with number " + sprintCreateDTO.getSprintNumber() + " already exists in this project.");
            });

        // Create the new Sprint entity
        Sprint newSprint = new Sprint(
            sprintCreateDTO.getSprintNumber(),
            sprintCreateDTO.getFinalGoal(),
            sprintCreateDTO.getStartDate(),
            sprintCreateDTO.getEndDate(),
            project // Link to the parent project
        );

        // Save the new sprint
        Sprint savedSprint = sprintRepository.save(newSprint);

        // Return the DTO
        return new SprintDTO(savedSprint);
    }
}