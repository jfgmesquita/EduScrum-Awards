package com.group7.eduscrum_awards.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import com.group7.eduscrum_awards.model.enums.Role;

/**
 * Represents a Student user.
 * This class extends the base User entity.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@DiscriminatorValue("STUDENT") // This identifies it as a 'STUDENT' in the 'users' table
public class Student extends User {

    // Maybe next fields:
    // private int totalPoints;
    // @OneToMany
    // private List<Award> awards;
    // @ManyToOne
    // private Degree degree;

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
}