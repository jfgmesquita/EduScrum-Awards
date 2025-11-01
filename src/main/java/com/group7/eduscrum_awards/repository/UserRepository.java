package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link User} entities.
 *
 * Extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD
 * operations and exposes a finder to look up users by their email.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique email address.
     *
     * @param email The email address to search for.
     * @return an {@link Optional} containing the matching {@link User} if found,
     * or an empty Optional if no user has that email.
     */
    Optional<User> findByEmail(String email);
    
    // Useful finders for the future can be added here, e.g.:
    // Optional<User> findByRole(Role role);
}