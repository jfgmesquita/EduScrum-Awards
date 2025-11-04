package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.TeamCreateDTO;
import com.group7.eduscrum_awards.dto.TeamDTO;
import com.group7.eduscrum_awards.exception.DuplicateResourceException;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Team;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.repository.UserRepository;
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
    private final UserRepository userRepository;

    @Autowired
    public TeamServiceImpl(TeamRepository teamRepository, ProjectRepository projectRepository, UserRepository userRepository) {
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
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

    /**
     * Assigns an existing Student to an existing Team.
     *
     * @param teamId The ID of the Team.
     * @param studentId The ID of the Student to assign.
     * @return A DTO of the updated Team.
     */
    @Override
    @Transactional
    public TeamDTO addStudentToTeam(Long teamId, Long studentId) {
        
        // Find the Team
        Team team = teamRepository.findById(teamId)
            .orElseThrow(() -> new ResourceNotFoundException("Team not found with id: " + teamId));

        // Find the User and validate it's a Student
        Student student = (Student) userRepository.findById(studentId)
            .filter(user -> user instanceof Student)
            .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));

        // Check for duplicate assignment
        if (team.getMembers().contains(student)) {
            throw new DuplicateResourceException("Student with id " + studentId + " is already in this team.");
        }

        // Add the relationship
        team.addStudent(student);
        
        // Save the "owning" side
        teamRepository.save(team);

        // Return the Team DTO
        return new TeamDTO(team);
    }
}