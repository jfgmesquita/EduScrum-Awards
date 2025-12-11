package com.group7.eduscrum_awards.dto.stats;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** DTO for degree-specific statistics. */
@AllArgsConstructor
@Getter
@Setter
public class DegreeStatsDTO {
    private long coursesCount;
    private long studentsCount;
    private long teachersCount;
}