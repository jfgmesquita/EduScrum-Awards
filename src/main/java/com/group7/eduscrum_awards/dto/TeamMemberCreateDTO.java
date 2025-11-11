package com.group7.eduscrum_awards.dto;

import com.group7.eduscrum_awards.model.enums.TeamRole;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for adding a Student to a Team with a specific role.
 * This is the request body for POST /api/v1/teams/{teamId}/members
 */
@Getter
@Setter
public class TeamMemberCreateDTO {

    @NotNull(message = "Student ID cannot be null.")
    private Long studentId;

    @NotNull(message = "Team Role cannot be null.")
    private TeamRole teamRole; // This will be "PRODUCT_OWNER", "SCRUM_MASTER" or "DEVELOPER"
}