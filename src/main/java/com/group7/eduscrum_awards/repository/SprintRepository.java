package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Sprint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * Repository interface for {@link Sprint} entities.
 *
 * Provides basic CRUD operations via JpaRepository and custom
 * finders for checking duplicate sprints within a project.
 */
@Repository
public interface SprintRepository extends JpaRepository<Sprint, Long> {

    /**
     * Finds a sprint by its number and its associated project.
     * This is used to enforce the business rule that sprint numbers
     * must be unique within a single project.
     *
     * @param sprintNumber The sprint number (e.g., 1, 2).
     * @param project The Project entity to check against.
     * @return an {@link Optional} containing the sprint if a match is found.
     */
    Optional<Sprint> findBySprintNumberAndProject(int sprintNumber, Project project);
}