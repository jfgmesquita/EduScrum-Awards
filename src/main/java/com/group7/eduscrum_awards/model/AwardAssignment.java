package com.group7.eduscrum_awards.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Represents an instance of an award given to a student.
 * e.g., "Student X received 'Fast Hands' on 2025-11-10 in Project Y".
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "award_assignments")
public class AwardAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate assignedAt;

    /** The award definition that was earned. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "award_id", nullable = false)
    private Award award;

    /** The student who received the award. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /**  
     * The project context where the award was earned.
     * Essential for filtering rankings by Course/Project.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * The team context (Optional).
     * Populated if the award was assigned to a whole team.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    public AwardAssignment(Award award, Student student, Project project, Team team) {
        this.award = award;
        this.student = student;
        this.project = project;
        this.team = team;
        this.assignedAt = LocalDate.now();
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
        AwardAssignment awardAssignment = (AwardAssignment) o;
        return id != null && id.equals(awardAssignment.id);
    }

    /** Uses the class hash when id is null to avoid collisions for transient instances. */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}