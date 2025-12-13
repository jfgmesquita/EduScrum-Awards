package com.group7.eduscrum_awards.dto.dashboard;

import com.group7.eduscrum_awards.model.Task;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO for displaying task details in the student dashboard. */
@NoArgsConstructor
@Getter
@Setter
public class DashboardTaskDTO {
    private Long id;
    private String description;
    private String status;
    private Long assignedMemberId;
    private String assignedMemberName;

    public DashboardTaskDTO(Task task) {
        this.id = task.getId();
        this.description = task.getDescription();
        this.status = task.getStatus().name();
        if (task.getTeamMember() != null) {
            this.assignedMemberId = task.getTeamMember().getId();
            // Check if the member has an associated student to get the name
            if (task.getTeamMember().getStudent() != null) {
                this.assignedMemberName = task.getTeamMember().getStudent().getName();
            }
        }
    }
}