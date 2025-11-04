package com.group7.eduscrum_awards.dto;

import com.group7.eduscrum_awards.model.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for sending basic Team data to the client.
 * This represents a team that already exists.
 */
@NoArgsConstructor
@Getter
@Setter
public class TeamDTO {

    private Long id;
    private String name;
    private int groupNumber;
    private Long projectId;

    /**
     * Convenience constructor to map from the Team entity.
     * @param team The Team entity.
     */
    public TeamDTO(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.groupNumber = team.getGroupNumber();
        
        if (team.getProject() != null) {
            this.projectId = team.getProject().getId();
        }
    }
}