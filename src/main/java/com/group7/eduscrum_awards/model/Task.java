package com.group7.eduscrum_awards.model;

import com.group7.eduscrum_awards.model.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** Represents a Task (User Story/Requirement) within a Sprint. */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob // For long text (User Story)
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;
    
    /** The current status of the task. */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private TaskStatus status = TaskStatus.TODO; // Default value

    /**
     * The Sprint this task belongs to.
     * This is the "Many" side of the One-to-Many relationship.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sprint_id", nullable = false)
    private Sprint sprint;

    /**
     * The team member responsible for this task.
     * This is optional (nullable = true) because a PO can create a task before it is assigned to a developer.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_member_id", nullable = true)
    private TeamMember teamMember;

    /**
     * Convenience constructor.
     * @param description The user story/requirement.
     * @param sprint The parent sprint.
     */
    public Task(String description, Sprint sprint) {
        this.description = description;
        this.sprint = sprint;
        this.teamMember = null;
    }

    /**
     * Equality is based on the identifier when it is set.
     * Two transient instances (with null id) are considered different.
     *
     * @param o object to compare
     * @return true if both objects are the same or have the same non-null id
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id != null && id.equals(task.id);
    }

    /** Uses the class hash when id is null to avoid collisions for transient instances. */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}