package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Degree;
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
     * Finds a course by its name and its associated degree.
     *
     * @param name The name of the course.
     * @param degree The Degree entit.
     * @return an {@link Optional} containing the course if a match is found.
     */
    Optional<Course> findByNameAndDegree(String name, Degree degree);
}