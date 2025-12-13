package com.group7.eduscrum_awards.dto.dashboard;

import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.enums.TeamRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Top-level DTO for the Student Dashboard.
 * Contains Project info, the Student's specific Role, and all Sprints/Tasks.
 */
@Getter
@Setter
@NoArgsConstructor
public class StudentDashboardProjectDTO {
    private Long projectId;
    private String projectName;
    private String projectDescription;
    private String myRole;
    private String myTeamName;
    private List<DashboardSprintDTO> sprints;

    public StudentDashboardProjectDTO(Project project, TeamRole role, String teamName) {
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.projectDescription = project.getDescription();
        this.myRole = role.name();
        this.myTeamName = teamName;
        this.sprints = project.getSprints().stream()
                .sorted(java.util.Comparator.comparing(sprint -> sprint.getSprintNumber()))
                .map(DashboardSprintDTO::new)
                .collect(Collectors.toList());
    }
}