package com.group7.eduscrum_awards.dto.studentdashboard;

import com.group7.eduscrum_awards.model.Project;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for Student Project information.
 * Maps Project entity to a simplified structure for student dashboard views,
 * including only sprints and tasks relevant to the specific student.
 */
@Getter
@Setter
public class StudentProjectDTO {
    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<StudentSprintDTO> sprints = new ArrayList<>();

    public StudentProjectDTO(Project project, Long studentId) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();

        if (project.getSprints() != null) {
            this.sprints = project.getSprints().stream()
                .map(sprint -> new StudentSprintDTO(sprint, studentId)) // We pass studentId here
                .collect(Collectors.toList());
        }
    }
}