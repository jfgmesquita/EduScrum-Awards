package com.group7.eduscrum_awards.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** DTO for global statistics. */
@AllArgsConstructor
@Getter
@Setter
public class GlobalStatsDTO {
    private long totalDegrees;
    private long totalCourses;
    private long totalStudents;
    private long totalTeachers;
}