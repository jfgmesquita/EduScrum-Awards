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
     * The set of projects within this course.
     * This is the "One" side of the One-to-Many relationship.
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Project> projects = new HashSet<>();

    /**
     * Custom awards defined for this course.
     * This is the "One" side of the One-to-Many relationship.
     */
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Award> awards = new HashSet<>();

    /**
     * Convenience constructor to create a Course with a name.
     * @param name The name of the course.
     */
    public Course(String name) {
        this.name = name;
    }

    /** Helper method to add a project to this course. */
    public void addProject(Project project) {
        this.projects.add(project);
        project.setCourse(this);
    }

    /** Helper method to remove a project from this course. */
    public void removeProject(Project project) {
        this.projects.remove(project);
        project.setCourse(null);
    }

    /** Helper method to add an award to this course. */
    public void addAward(Award award) {
        this.awards.add(award);
        award.setCourse(this);
    }

    /** Helper method to remove an award from this course. */
    public void removeAward(Award award) {
        this.awards.remove(award);
        award.setCourse(null);
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