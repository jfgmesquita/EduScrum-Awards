package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.model.User;
import com.group7.eduscrum_awards.model.enums.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    
    /**
     * Counts users by their role.
     * 
     * @param role the role to count users by
     * @return the count of users with the specified role
     */
    long countByRole(Role role);
    
    /**
     * Counts students by their degree ID and role.
     * 
     * @param degreeId the ID of the degree
     * @param role the role to count students by
     * @return the count of students in the given degree with the specified role
     */
    @Query("SELECT COUNT(s) FROM Student s WHERE s.degree.id = :degreeId AND s.role = :role")
    long countByDegreeIdAndRole(@Param("degreeId") Long degreeId, @Param("role") Role role);

    /**
     * Counts distinct teachers associated with courses in a given degree.
     * 
     * @param degreeId the ID of the degree
     * @return the count of distinct teachers in the given degree
     */
    @Query("SELECT COUNT(DISTINCT t) FROM Course c JOIN c.teachers t WHERE c.degree.id = :degreeId")
    long countDistinctTeachersByDegreeId(@Param("degreeId") Long degreeId);

    /**
     * Finds all users by their role.
     * 
     * @param role the role to filter users by
     * @return a list of users with the specified role
     */
    List<User> findAllByRole(Role role);
}