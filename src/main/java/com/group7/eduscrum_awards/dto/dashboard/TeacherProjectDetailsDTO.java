package com.group7.eduscrum_awards.dto.dashboard;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Complex DTO for the Teacher Dashboard View of a Project.
 * Aggregates Project info, Teams, and Sprints (with Tasks).
 */
@Getter
@Setter
@NoArgsConstructor
public class TeacherProjectDetailsDTO {
    private Long id;
    private String name;
    private String description;
    
    // Aggregated lists
    private List<TeamDTO> teams;
    private List<DashboardSprintDTO> sprints; // Reusing existing DTO
}