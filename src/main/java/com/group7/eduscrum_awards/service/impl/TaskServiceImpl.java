package com.group7.eduscrum_awards.service.impl;

import com.group7.eduscrum_awards.dto.TaskCreateDTO;
import com.group7.eduscrum_awards.dto.TaskDTO;
import com.group7.eduscrum_awards.exception.ResourceNotFoundException;
import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Sprint;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.Task;
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

        // 7. Return the DTO
        return new TaskDTO(savedTask);
    }
}