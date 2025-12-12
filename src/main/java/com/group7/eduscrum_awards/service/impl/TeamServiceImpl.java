package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.TeamCreateDTO;
import com.group7.eduscrum_awards.dto.TeamDTO;
import com.group7.eduscrum_awards.dto.TeamMemberCreateDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Team;
import com.group7.eduscrum_awards.model.TeamMember;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.repository.UserRepository;
import com.group7.eduscrum_awards.repository.ProjectRepository;
import com.group7.eduscrum_awards.repository.TeamRepository;
import com.group7.eduscrum_awards.repository.TeamMemberRepository;
import com.group7.eduscrum_awards.service.TeamService;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the TeamService.
 * Handles the logic for creating teams and adding members.
 */
@Service
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository, ProjectRepository projectRepository, UserRepository userRepository, TeamMemberRepository teamMemberRepository) {
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
        this.teamMemberRepository = teamMemberRepository;
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

    /** Assigns an existing Student to an existing Team with a specific role. */
    @Override
    @Transactional
    public TeamDTO addMemberToTeam(Long teamId, TeamMemberCreateDTO createDTO) {
        
        // Find the Team
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        // Find the Student
        Student student = (Student) userRepository.findById(createDTO.getStudentId())
            .filter(user -> user instanceof Student)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + createDTO.getStudentId()));
            
        // Find the Project
        Project project = team.getProject();

        // Check if the Student is already in a Team for this Project
        teamMemberRepository.findByStudentAndProject(student, project)
            .ifPresent(member -> {
                // The student is already assigned to a team in this project
                throw new DuplicateResourceException("Student with id " + student.getId() + " is already on a team for this project (Team ID: " + member.getTeam().getId() + ")");
            });

        // Create the new association entity
        TeamMember newMembership = new TeamMember();
        newMembership.setTeam(team);
        newMembership.setStudent(student);
        newMembership.setTeamRole(createDTO.getTeamRole());
        newMembership.setProject(project);

        // Save the new entity directly
        teamMemberRepository.save(newMembership);

        // Add to the 'in-memory' set if needed
        team.getMembers().add(newMembership);

        // Return the Team DTO
        return new TeamDTO(team);
    }

    /**
     * Retrieves all teams associated with a specific project.
     * 
     * @param projectId The ID of the project.
     * @return A list of TeamDTOs representing the teams in the project.
     */
    @Override
    @Transactional(readOnly = true)
    public List<TeamDTO> getTeamsByProject(Long projectId) {
        return teamRepository.findByProjectId(projectId).stream()
                .map(TeamDTO::new).collect(Collectors.toList());
    }
}