package com.group7.eduscrum_awards.dto.teacher;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

/** Data Transfer Object for summarizing Project information for teachers. */
@AllArgsConstructor
@Getter 
@Setter 
public class ProjectSummaryDTO {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
    private long numberOfTeams;
}