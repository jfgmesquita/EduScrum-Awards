package com.group7.eduscrum_awards.dto;

import com.group7.eduscrum_awards.model.TeamMember;
import com.group7.eduscrum_awards.model.enums.TeamRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for viewing Team Member information.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeamMemberViewDTO {
    private Long userId;
    private String name;
    private TeamRole role;
    private Long teamMemberId;

    public TeamMemberViewDTO(TeamMember member) {
        this.teamMemberId = member.getId();
        this.role = member.getTeamRole();
        if (member.getStudent() != null) {
            this.userId = member.getStudent().getId();
            this.name = member.getStudent().getName();
        }
    }
}