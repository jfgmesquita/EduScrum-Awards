package com.group7.eduscrum_awards.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToMany;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;
import com.group7.eduscrum_awards.model.enums.Role;

/**
 * Represents a Teacher user.
 * This class extends the base User entity.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@DiscriminatorValue("TEACHER") // This identifies it as a 'TEACHER' in the 'users' table
public class Teacher extends User {

    /**
     * The courses this teacher is associated with.
     * 'mappedBy = "teachers"': Tells JPA that the 'Course' side
     * is the owner of this ManyToMany relationship.
     */
    @ManyToMany(mappedBy = "teachers", fetch = FetchType.LAZY) // Performance: Don't load courses unless asked
    private List<Course> courses = new ArrayList<>();

    public Teacher(String name, String email, String password) {
        super(name, email, password, Role.TEACHER);
    }

    /**
     * Adds a course to this teacher's list.
     * @param course The course to add.
     */
    public void addCourse(Course course) {
        this.courses.add(course);
        course.getTeachers().add(this); // Keep the other side in sync
    }

    /**
     * Removes a course from this teacher's list.
     * @param course The course to remove.
     */
    public void removeCourse(Course course) {
        this.courses.remove(course);
        course.getTeachers().remove(this); // Keep the other side in sync
    }
}