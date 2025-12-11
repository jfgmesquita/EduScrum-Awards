package com.group7.eduscrum_awards.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** DTO for teacher-specific statistics. */
@AllArgsConstructor
@Getter
@Setter
public class TeacherStatsDTO {
    private long coursesCount;
}