package com.group7.eduscrum_awards.dto.studentdashboard;

import com.group7.eduscrum_awards.model.Task;
import lombok.Getter;
import lombok.Setter;

/**
 * Data Transfer Object for Student Task information.
 * Maps Task entity to a simplified structure for student dashboard views.
 */
@Getter
@Setter
public class StudentTaskDTO {
    private Long id;
    private String description;
    private String status;
    private String teamMemberName;

    public StudentTaskDTO(Task task) {
        this.id = task.getId();
        this.description = task.getDescription();
        this.status = task.getStatus().name();
        if (task.getTeamMember() != null) {
            this.teamMemberName = task.getTeamMember().getStudent().getName();
        }
    }
}