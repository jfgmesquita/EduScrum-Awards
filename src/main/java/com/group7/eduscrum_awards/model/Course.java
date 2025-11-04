package com.group7.eduscrum_awards.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Course in the system (e.g., "Software Quality").
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    /**
     * The list of teachers associated with this course.
     * This side is the "owner" of the relationship.
     */
    @ManyToMany(fetch = FetchType.LAZY) // Performance: Don't load teachers unless asked
    @JoinTable(
        name = "course_teachers", // Name of the new join table
        joinColumns = @JoinColumn(name = "course_id"), // FK to this class
        inverseJoinColumns = @JoinColumn(name = "teacher_id") // FK to the other class
    )
    private List<Teacher> teachers = new ArrayList<>();

    /**
     * Convenience constructor to create a Course with a name.
     * @param name The name of the course.
     */
    public Course(String name) {
        this.name = name;
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
        Course course = (Course) o;
        return id != null && id.equals(course.id);
    }

    /** Uses the class hash when id is null to avoid collisions for transient instances. */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}