package com.group7.eduscrum_awards.model;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;

/** Represents a Course in the system (e.g., "Software Quality"). */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** The set of teachers associated with this course. */
    @ManyToMany(fetch = FetchType.LAZY) 
    @JoinTable(
        name = "course_teachers", 
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "teacher_id")
    )
    private Set<Teacher> teachers = new HashSet<>();

    /**
     * The degree this course belongs to.
     * This is the "many" side of the One-to-Many relationship.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "degree_id", nullable = false)
    private Degree degree;

    /** The set of students enrolled in this course. */
    @ManyToMany(
        mappedBy = "courses", // Mapped by the 'courses' field in the Student class
        fetch = FetchType.LAZY
    )
    private Set<Student> students = new HashSet<>();

    /**
     * Convenience constructor to create a Course with a name.
     * @param name The name of the course.
     */
    public Course(String name) {
        this.name = name;
    }

    /** Equality is based on the identifier. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return id != null && id.equals(course.id);
    }

    /** Uses the class hash. */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}