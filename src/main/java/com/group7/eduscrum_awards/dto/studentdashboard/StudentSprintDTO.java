package com.group7.eduscrum_awards.dto.studentdashboard;

import com.group7.eduscrum_awards.model.Sprint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Data Transfer Object for Student Sprint information.
 * Maps Sprint entity to a simplified structure for student dashboard views,
 * including only tasks assigned to the specific student.
 */
@Getter
@Setter
public class StudentSprintDTO {
    private Long id;
    private int sprintNumber;
    private String goal;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<StudentTaskDTO> tasks = new ArrayList<>();

    /**
     * Constructor that maps Sprint entity to StudentSprintDTO,
     * filtering tasks to include only those assigned to the specified student.
     * 
     * @param sprint the Sprint entity
     * @param studentId the ID of the student for whom tasks should be filtered
     */
    public StudentSprintDTO(Sprint sprint, Long studentId) {
        this.id = sprint.getId();
        this.sprintNumber = sprint.getSprintNumber();
        this.goal = sprint.getFinalGoal();
        this.startDate = sprint.getStartDate();
        this.endDate = sprint.getEndDate();

        // only include tasks assigned to the specified student
        if (sprint.getTasks() != null) {
            this.tasks = sprint.getTasks().stream()
                .filter(t -> t.getTeamMember() != null && 
                            t.getTeamMember().getStudent() != null &&
                            t.getTeamMember().getStudent().getId().equals(studentId))
                .map(StudentTaskDTO::new)
                .sorted(Comparator.comparing(StudentTaskDTO::getId))
                .collect(Collectors.toList());
        }
    }
}