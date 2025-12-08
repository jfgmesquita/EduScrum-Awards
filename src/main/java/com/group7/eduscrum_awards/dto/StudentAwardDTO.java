package com.group7.eduscrum_awards.dto;

import com.group7.eduscrum_awards.model.AwardAssignment;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/** DTO for sending Student Award data to the client. */
@NoArgsConstructor
@Getter
@Setter
public class StudentAwardDTO {
    private String awardName;
    private String awardDescription;
    private int points;
    private String badgeIcon;
    private LocalDateTime assignedAt;
    private String projectName;

    public StudentAwardDTO(AwardAssignment assignment) {
        this.awardName = assignment.getAward().getName();
        this.awardDescription = assignment.getAward().getDescription();
        this.points = assignment.getAward().getPoints();
        this.badgeIcon = assignment.getAward().getBadgeIcon();
        this.assignedAt = assignment.getAssignedAt();
        
        if (assignment.getProject() != null) {
            this.projectName = assignment.getProject().getName();
        }
    }
}