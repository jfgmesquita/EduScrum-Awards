package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for {@link Project} entities.
 *
 * Extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD operations and custom finders.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Finds a project by its name and its associated course.
     * Used to check for duplicate project names within a single course.
     *
     * @param name The name of the project.
     * @param course The Course entity to check against.
     * @return an {@link Optional} containing the project if a match is found.
     */
    Optional<Project> findByNameAndCourse(String name, Course course);
}