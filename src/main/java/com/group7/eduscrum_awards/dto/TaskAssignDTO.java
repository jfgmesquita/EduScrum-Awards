package com.group7.eduscrum_awards.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for assigning a Task to a TeamMember (Developer).
 * This is the request body for PATCH /api/v1/tasks/{taskId}/assign
 */
@Getter
@Setter
public class TaskAssignDTO {

    @NotNull(message = "Team Member ID (Developer ID) cannot be null.")
    private Long teamMemberId;
}