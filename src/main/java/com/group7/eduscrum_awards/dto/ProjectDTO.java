package com.group7.eduscrum_awards.dto;

import com.group7.eduscrum_awards.model.Project;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

/**
 * DTO for sending Project data to the client.
 * This represents a project that already exists.
 */
@Getter
@Setter
@NoArgsConstructor
public class ProjectDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long courseId;

    /**
     * Convenience constructor to map from the Project entity.
     * @param project The Project entity.
     */
    public ProjectDTO(Project project) {
        this.id = project.getId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.startDate = project.getStartDate();
        this.endDate = project.getEndDate();
        
        if (project.getCourse() != null) {
            this.courseId = project.getCourse().getId();
        }
    }
}