package com.group7.eduscrum_awards.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO to transfer specific project details: Course name and Team count.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjectCourseTeamsDTO {
    private String courseName;
    private Long numberOfTeams;
}