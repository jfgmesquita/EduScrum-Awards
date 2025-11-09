package com.group7.eduscrum_awards.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
// import java.util.Set; // Will be used for Tasks
// import java.util.HashSet;

/** Represents a Sprint within a Project. */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "sprints", uniqueConstraints = {@UniqueConstraint(columnNames = {"project_id", "sprint_number"})})
public class Sprint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sprint_number", nullable = false)
    private int sprintNumber;

    @Lob // For long text
    @Column(name = "final_goal", columnDefinition = "TEXT")
    private String finalGoal;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /** The Project this sprint belongs to. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
    
    // This is for the next requirement ("list of tasks")
    // @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL, orphanRemoval = true)
    // private Set<Task> tasks = new HashSet<>();

    /**
    * Convenience constructor.
    * @param sprintNumber The sprint's number (e.g., 1, 2, 3).
    * @param finalGoal The sprint's final goal description.
    * @param startDate The sprint's start date.
    * @param endDate The sprint's end date.
    * @param project The project this sprint is for.
     */
    public Sprint(int sprintNumber, String finalGoal, LocalDate startDate, LocalDate endDate, Project project) {
        this.sprintNumber = sprintNumber;
        this.finalGoal = finalGoal;
        this.startDate = startDate;
        this.endDate = endDate;
        this.project = project;
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
        Sprint sprint = (Sprint) o;
        return id != null && id.equals(sprint.id);
    }

    /** Uses the class hash when id is null to avoid collisions for transient instances. */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}