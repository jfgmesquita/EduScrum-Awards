package com.group7.eduscrum_awards.model;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    // To link this to a Team later:
    // @OneToOne(mappedBy = "project")
    // private Team team;

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