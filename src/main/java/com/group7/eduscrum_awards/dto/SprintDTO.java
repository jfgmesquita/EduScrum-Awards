package com.group7.eduscrum_awards.dto;

import com.group7.eduscrum_awards.model.Sprint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

/**
 * DTO for sending Sprint data to the client.
 * This represents a sprint that already exists.
 */
@Getter
@Setter
@NoArgsConstructor
public class SprintDTO {

    private Long id;
    private int sprintNumber;
    private String finalGoal;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long projectId;

    /**
     * Convenience constructor to map from the Sprint entity.
     * @param sprint The Sprint entity.
     */
    public SprintDTO(Sprint sprint) {
        this.id = sprint.getId();
        this.sprintNumber = sprint.getSprintNumber();
        this.finalGoal = sprint.getFinalGoal();
        this.startDate = sprint.getStartDate();
        this.endDate = sprint.getEndDate();
        
        if (sprint.getProject() != null) {
            this.projectId = sprint.getProject().getId();
        }
    }
}