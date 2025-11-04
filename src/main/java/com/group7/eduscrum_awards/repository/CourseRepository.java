package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for {@link Course} entities.
 *
 * Extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD
 * operations and exposes a finder to look up courses by their name.
 */
@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    /**
     * Finds a course by its unique name.
     *
     * @param name The course name to search for.
     * @return an {@link Optional} containing the matching {@link Course} if found,
     * or an empty Optional if no match is found.
     */
    Optional<Course> findByName(String name);
}