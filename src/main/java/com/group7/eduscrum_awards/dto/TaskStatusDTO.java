package com.group7.eduscrum_awards.dto;

import com.group7.eduscrum_awards.model.enums.TaskStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Data Transfer Object for Task Status updates. */
@Data
public class TaskStatusDTO {
    @NotNull(message = "Status is required")
    private TaskStatus status;
}