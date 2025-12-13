package com.group7.eduscrum_awards.dto.rankings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/** Root DTO for the Student Rankings Dashboard. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StudentDashboardRankingDTO {
    private List<StudentScoreDTO> individualRankings;
    private List<CourseRankingDTO> teamRankingsByCourse;
}