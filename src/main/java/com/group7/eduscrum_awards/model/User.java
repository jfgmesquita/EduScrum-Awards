package com.group7.eduscrum_awards.model;

import com.group7.eduscrum_awards.model.enums.Role;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

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
public class User implements UserDetails {

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
        User user = (User) o;
        return id != null && id.equals(user.id);
    }

    /** Uses the class hash when id is null to avoid collisions for transient instances. */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Returns the authorities granted to the user.
     * Spring Security requires roles to be prefixed with "ROLE_".
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    /** Returns the username used to authenticate the user (in our case, the email). */
    @Override
    public String getUsername() {
        return this.email;
    }

    /** Returns the password used to authenticate the user. */
    @Override
    public String getPassword() {
        return this.password;
    }

    // Account status methods

    /** Indicates whether the user's account has expired. */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /** Indicates whether the user is locked or unlocked. */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /** Indicates whether the user's credentials (password) has expired. */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /** Indicates whether the user is enabled or disabled. */
    @Override
    public boolean isEnabled() {
        return true;
    }
}