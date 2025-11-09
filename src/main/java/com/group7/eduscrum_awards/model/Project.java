package com.group7.eduscrum_awards.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Set;
import java.util.HashSet;

/** Represents a Scrum Project within a Course. */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Lob // Large text field
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "start_date")
    private LocalDate startDate;
    
    @Column(name = "end_date")
    private LocalDate endDate;

    /**
     * The Course this project belongs to.
     * This is the "Many" side of the One-to-Many relationship.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    /**
     * The set of teams working on this project.
     * This is the "One" side of the One-to-Many relationship.
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Team> teams = new HashSet<>();

    /**
     * The set of sprints within this project.
     * This is the "One" side of the One-to-Many relationship.
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Sprint> sprints = new HashSet<>();

    /**
     * Convenience constructor.
     * @param name The project name.
     * @param description The project description.
     * @param course The parent course.
     */
    public Project(String name, String description, Course course, LocalDate startDate, LocalDate endDate) {
        this.name = name;
        this.description = description;
        this.course = course;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Adds a team to this project.
     * @param team
     */
    public void addTeam(Team team) {
        this.teams.add(team);
        team.setProject(this);
    }

    /**
     * Removes a team from this project.
     * @param team
     */
    public void removeTeam(Team team) {
        this.teams.remove(team);
        team.setProject(null);
    }

    /**
     * Adds a sprint to this project.
     * @param sprint
     */
    public void addSprint(Sprint sprint) {
        this.sprints.add(sprint);
        sprint.setProject(this);
    }

    /**
     * Removes a sprint from this project.
     * @param sprint
     */
    public void removeSprint(Sprint sprint) {
        this.sprints.remove(sprint);
        sprint.setProject(null);
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
        Project project = (Project) o;
        return id != null && id.equals(project.id);
    }

    /** Uses the class hash when id is null to avoid collisions for transient instances. */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}