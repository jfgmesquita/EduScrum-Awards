package com.group7.eduscrum_awards.dto.dashboard;

import com.group7.eduscrum_awards.model.Sprint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/** DTO for displaying sprint details including tasks. */
@NoArgsConstructor
@Getter
@Setter
public class DashboardSprintDTO {
    private Long id;
    private Integer sprintNumber;
    private String finalGoal;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<DashboardTaskDTO> tasks;

    public DashboardSprintDTO(Sprint sprint) {
        this.id = sprint.getId();
        this.sprintNumber = sprint.getSprintNumber();
        this.finalGoal = sprint.getFinalGoal();
        this.startDate = sprint.getStartDate();
        this.endDate = sprint.getEndDate();
        this.tasks = sprint.getTasks().stream()
                .map(DashboardTaskDTO::new)
                .collect(Collectors.toList());
    }
}