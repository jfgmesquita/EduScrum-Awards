package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.TaskCreateDTO;
import com.group7.eduscrum_awards.dto.TaskDTO;
import com.group7.eduscrum_awards.dto.TaskAssignDTO;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Sprint;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.Task;
import com.group7.eduscrum_awards.model.TeamMember;
import com.group7.eduscrum_awards.model.User;
import com.group7.eduscrum_awards.model.enums.TeamRole;
import com.group7.eduscrum_awards.repository.SprintRepository;
import com.group7.eduscrum_awards.repository.TaskRepository;
import com.group7.eduscrum_awards.repository.TeamMemberRepository;
import com.group7.eduscrum_awards.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the TaskService.
 * Handles the logic for creating tasks.
 */
@Service
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;
    private final SprintRepository sprintRepository;
    private final TeamMemberRepository teamMemberRepository;

    @Autowired
    public TaskServiceImpl(TaskRepository taskRepository, SprintRepository sprintRepository, TeamMemberRepository teamMemberRepository) {
        this.taskRepository = taskRepository;
        this.sprintRepository = sprintRepository;
        this.teamMemberRepository = teamMemberRepository;
    }

    /**
     * Creates a new task for a specific sprint,
     * verifying that the user is the Product Owner.
     */
    @Override
    @Transactional
    public TaskDTO createTask(Long sprintId, TaskCreateDTO taskCreateDTO) {
        
        // Find the parent Sprint
        Sprint sprint = sprintRepository.findById(sprintId)
            .orElseThrow(() -> new ResourceNotFoundException("Sprint not found with id: " + sprintId));

        // Get the Project from the Sprint
        Project project = sprint.getProject();
        if (project == null) {
            throw new IllegalStateException("Sprint with id " + sprintId + " is not associated with a project.");
        }

        // Get the currently logged-in user
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // Authorization Check: Verify the user is the PO for this project
        if (!(currentUser instanceof Student)) {
            throw new AccessDeniedException("Only students (Product Owners) can create tasks.");
        }
        
        boolean isProductOwner = teamMemberRepository.findByStudentAndProject((Student) currentUser, project)
            .map(member -> member.getTeamRole() == TeamRole.PRODUCT_OWNER)
            .orElse(false);

        if (!isProductOwner) {
            throw new AccessDeniedException("You are not the Product Owner for this project.");
        }

        // Authorization passed. Create the new Task.
        Task newTask = new Task(taskCreateDTO.getDescription(), sprint);

        // Save the new task
        Task savedTask = taskRepository.save(newTask);

        // Return the DTO
        return new TaskDTO(savedTask);
    }

    /**
     * Assigns an existing Task to a specific TeamMember (Developer).
     *
     * @param taskId The ID of the Task to be updated.
     * @param assignDTO The DTO containing the ID of the TeamMember to assign.
     * @return The DTO of the updated Task.
     */
    @Override
    @Transactional
    public TaskDTO assignTask(Long taskId, TaskAssignDTO assignDTO) {
        
        // Find the Task to be assigned
        Task task = taskRepository.findById(taskId)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

        // Find the target TeamMember (the Developer)
        TeamMember developer = teamMemberRepository.findById(assignDTO.getTeamMemberId())
            .orElseThrow(() -> new ResourceNotFoundException("TeamMember (Developer) not found with id: " + assignDTO.getTeamMemberId()));

        // Get the Project from the task's sprint
        Project taskProject = task.getSprint().getProject();
        if (taskProject == null) {
            throw new IllegalStateException("Task's sprint is not associated with a project.");
        }

        // Get the currently logged-in user (the Product Owner)
        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(currentUser instanceof Student)) {
            throw new AccessDeniedException("Only students (Product Owners) can assign tasks.");
        }
        
        // Authorization Check 1: Verify the logged-in user is the PO for this project
        Student productOwner = (Student) currentUser;
        boolean isProductOwner = teamMemberRepository.findByStudentAndProject(productOwner, taskProject)
            .map(member -> member.getTeamRole() == TeamRole.PRODUCT_OWNER)
            .orElse(false);

        if (!isProductOwner) {
            throw new AccessDeniedException("You are not the Product Owner for this project.");
        }

        // Authorization Check 2: Verify the developer belongs to the same project
        if (!developer.getProject().getId().equals(taskProject.getId())) {
            throw new IllegalArgumentException("The selected developer (TeamMemberId: " + developer.getId() + ") does not belong to this project.");
        }
        
        // Authorization Check 3: Verify the developer is a DEVELOPER
        if (developer.getTeamRole() != TeamRole.DEVELOPER) {
             throw new IllegalArgumentException("The selected team member is not a Developer. Role: " + developer.getTeamRole());
        }

        // All checks passed. Assign the task.
        task.setTeamMember(developer);
        Task savedTask = taskRepository.save(task);

        // Return the updated DTO
        return new TaskDTO(savedTask);
    }
}