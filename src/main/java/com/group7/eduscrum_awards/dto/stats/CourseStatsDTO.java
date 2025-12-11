package com.group7.eduscrum_awards.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** DTO for course-specific statistics. */
@AllArgsConstructor
@Getter
@Setter
public class CourseStatsDTO {
    private long studentsCount;
    private long teachersCount;
    private String degreeName;
}