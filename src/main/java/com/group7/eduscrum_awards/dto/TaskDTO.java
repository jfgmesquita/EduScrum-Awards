package com.group7.eduscrum_awards.dto;

import com.group7.eduscrum_awards.model.Task;
import com.group7.eduscrum_awards.model.enums.TaskStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for sending Task data to the client.
 * This represents a task that already exists.
 */
@NoArgsConstructor
@Getter
@Setter
public class TaskDTO {

    private Long id;
    private String description;
    private TaskStatus status;
    private Long sprintId;
    private Long teamMemberId; // Will be null if the task is unassigned

    /**
     * Convenience constructor to map from the Task entity.
     * @param task The Task entity.
     */
    public TaskDTO(Task task) {
        this.id = task.getId();
        this.description = task.getDescription();
        this.status = task.getStatus();
        
        // The sprint is non-null, so we can get its ID directly
        if (task.getSprint() != null) {
            this.sprintId = task.getSprint().getId();
        }
        
        // The teamMember is optional, so we must check for null
        if (task.getTeamMember() != null) {
            this.teamMemberId = task.getTeamMember().getId();
        } else {
            this.teamMemberId = null;
        }
    }
}