package com.group7.eduscrum_awards.dto.dashboard;

import com.group7.eduscrum_awards.model.Team;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO for displaying Team information in lists. */
@NoArgsConstructor
@Getter
@Setter
public class TeamDTO {
    private Long id;
    private String name;
    private int groupNumber;
    private int memberCount;

    public TeamDTO(Team team) {
        this.id = team.getId();
        this.name = team.getName();
        this.groupNumber = team.getGroupNumber();
        this.memberCount = (team.getMembers() != null) ? team.getMembers().size() : 0;
    }
}