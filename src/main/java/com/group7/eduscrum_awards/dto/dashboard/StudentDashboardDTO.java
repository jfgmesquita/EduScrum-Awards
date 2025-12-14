package com.group7.eduscrum_awards.dto.dashboard;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Main DTO for the Student Dashboard.
 * Aggregates global stats and the list of projects.
 */
@Getter
@Setter
@NoArgsConstructor
public class StudentDashboardDTO {
    // Global Stats
    private long totalScore; // Card 1 - Total Score
    private int ranking; // Card 4 - Ranking
    private long totalStudents; // Card 4 - Number of Students in Degree
    
    // Awards Stats (Card 2)
    private int totalAwards;
    private int manualAwards;
    private int automaticAwards;

    // Task Stats (Card 3)
    private long tasksCompleted;
    private long tasksTotal;

    // Existing project list
    private List<StudentDashboardProjectDTO> projects;
}