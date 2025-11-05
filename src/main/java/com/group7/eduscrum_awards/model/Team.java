package com.group7.eduscrum_awards.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a Project Team.
 * A team belongs to one Project and has many Students (members).
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "teams")
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "group_number", nullable = false)
    private int groupNumber;

    /**
     * The Project this team belongs to.
     * This is the "Many" side of the One-to-Many relationship.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    /**
     * The set of students who are members of this team.
     * This is the "owning" side of the Many-to-Many relationship.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "team_members",
        joinColumns = @JoinColumn(name = "team_id"), // FK to this entity
        inverseJoinColumns = @JoinColumn(name = "student_id") // FK to the other entity
    )
    private Set<Student> members = new HashSet<>();

    /**
     * Convenience constructor.
     * @param name Team name (e.g., "Relâmpagos Marquinhos").
     * @param groupNumber The team's number (e.g., 1, 2, 3).
     * @param project The project this team is for.
     */
    public Team(String name, int groupNumber, Project project) {
        this.name = name;
        this.groupNumber = groupNumber;
        this.project = project;
    }
    
    /** 
    * Adds a student to the team.
    * @param student The student to add
    */
    public void addStudent(Student student) {
        this.members.add(student);
        student.getTeams().add(this);
    }

    /** 
    * Removes a student from the team.
    * @param student The student to remove
    */
    public void removeStudent(Student student) {
        this.members.remove(student);
        student.getTeams().remove(this);
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
        Team team = (Team) o;
        return id != null && id.equals(team.id);
    }

    /** Uses the class hash when id is null to avoid collisions for transient instances. */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}