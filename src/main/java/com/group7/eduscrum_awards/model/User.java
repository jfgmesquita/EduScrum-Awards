package com.group7.eduscrum_awards.model;

import com.group7.eduscrum_awards.model.enums.Role;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a User in the system.
 * This is the base class for all users (Admin, Teacher, Student).
 *
 * We use a SINGLE_TABLE inheritance strategy, 
 * meaning all user types are stored in the 'users' table.
 */
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "email", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    /**
     * The user's role (e.g., ADMIN, TEACHER, STUDENT).
     * @Enumerated(EnumType.STRING): Stores the role as a string ("ADMIN") in the database
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    /**
     * Convenience constructor.
     *
     * @param name The user's full name.
     * @param email The user's email (must be unique).
     * @param password The user's password (hashed).
     * @param role The user's role.
     */
    public User(String name, String email, String password, Role role) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    /** Equality is based on the identifier */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    /** Uses the class hash code */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}