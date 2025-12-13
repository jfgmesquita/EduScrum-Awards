package com.group7.eduscrum_awards.dto.rankings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/** DTO for a specific team's ranking details. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TeamRankingDTO {
    private int rank;
    private Long teamId;
    private String teamName;
    private Long totalScore;
    private int memberCount;
    private List<TeamMemberScoreDTO> members;
}