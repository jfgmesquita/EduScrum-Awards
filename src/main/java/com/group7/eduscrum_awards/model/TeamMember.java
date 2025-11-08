package com.group7.eduscrum_awards.model;

import com.group7.eduscrum_awards.model.enums.TeamRole;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/** 
 * Association Entity linking a Student to a Team with a specific role.
 * This table enforces that a student can only be on one team per project.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "team_members", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "project_id"}) // A student can only be on one team per project
})
public class TeamMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** The team this membership belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id", nullable = false)
    private Team team;

    /** The student this membership belongs to */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    /**
     * The project this membership is for.
     * This is required to enforce the unique constraint.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /** The role this student has in this team */
    @Enumerated(EnumType.STRING)
    @Column(name = "team_role", nullable = false)
    private TeamRole teamRole;

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
        TeamMember that = (TeamMember) o;
        return id != null && id.equals(that.id);
    }

    /** Uses the class hash when id is null to avoid collisions for transient instances. */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}