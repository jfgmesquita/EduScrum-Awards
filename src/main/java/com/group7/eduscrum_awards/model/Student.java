package com.group7.eduscrum_awards.model;

import com.group7.eduscrum_awards.model.enums.Role;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a Student user.
 * This class extends the base User entity.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@DiscriminatorValue("STUDENT")
public class Student extends User {

    /**
     * The Degree this Student is enrolled in.
     * This is the "Many" side of the One-to-Many relationship with Degree.
     * It adds a 'degree_id' foreign key column to the 'users' table.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "degree_id") // This FK column will be on the 'users' table
    private Degree degree;

    /**
     * The set of courses this student is enrolled in.
     * This is the "owning" side of the Many-to-Many relationship with Course.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "student_courses", // Name of the new join table
        joinColumns = @JoinColumn(name = "student_id"), // FK to this entity (users table)
        inverseJoinColumns = @JoinColumn(name = "course_id") // FK to the other entity (courses table)
    )
    private Set<Course> courses = new HashSet<>();

    /** The list of team memberships this student has across all projects. */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TeamMember> teamMemberships = new HashSet<>();

    /** History of awards received by this student. */
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<AwardAssignment> awardAssignments = new HashSet<>();

    /**
     * Convenience constructor to create a new Student.
     * Automatically sets the Role to STUDENT.
     *
     * @param name The student's name.
     * @param email The student's email (must be unique).
     * @param password The student's hashed password.
     */
    public Student(String name, String email, String password) {
        super(name, email, password, Role.STUDENT);
    }

    /**
     * Enrolls this student in a course.
     * @param course The course to enroll in.
     */
    public void addCourse(Course course) {
        this.courses.add(course);
        course.getStudents().add(this); // Keep both sides in sync
    }

    /**
     * Unenrolls this student from a course.
     * @param course The course to unenroll from.
     */
    public void removeCourse(Course course) {
        this.courses.remove(course);
        course.getStudents().remove(this); // Keep both sides in sync
    }
}