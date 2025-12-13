package com.group7.eduscrum_awards.dto.rankings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** DTO for a team member's score within a team ranking. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TeamMemberScoreDTO {
    private Long studentId;
    private String studentName;
    private Long score;
}