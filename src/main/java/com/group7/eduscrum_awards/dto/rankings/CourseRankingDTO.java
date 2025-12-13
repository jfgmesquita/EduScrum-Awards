package com.group7.eduscrum_awards.dto.rankings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

/** DTO grouping team rankings by course. */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CourseRankingDTO {
    private Long courseId;
    private String courseName;
    private List<TeamRankingDTO> rankings;
}