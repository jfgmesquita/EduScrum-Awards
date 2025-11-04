package com.group7.eduscrum_awards.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;
import com.group7.eduscrum_awards.model.enums.Role;

/**
 * Represents a Teacher user.
 * This class extends the base User entity.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@DiscriminatorValue("TEACHER")
public class Teacher extends User {

    /** The set of courses this teacher is associated with. */
    @ManyToMany(mappedBy = "teachers", fetch = FetchType.LAZY)
    private Set<Course> courses = new HashSet<>();

    /** Convenience constructor to create a new Teacher. */
    public Teacher(String name, String email, String password) {
        super(name, email, password, Role.TEACHER);
    }

    /**
     * Adds a course to this teacher's set.
     * If the course is already in the set, this operation does nothing.
     * @param course The course to add.
     */
    public void addCourse(Course course) {
        this.courses.add(course);
        course.getTeachers().add(this);
    }

    /**
     * Removes a course from this teacher's set.
     * @param course The course to remove.
     */
    public void removeCourse(Course course) {
        this.courses.remove(course);
        course.getTeachers().remove(this);
    }
}