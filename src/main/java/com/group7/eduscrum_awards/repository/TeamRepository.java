package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for {@link Team} entities.
 *
 * Provides basic CRUD operations via JpaRepository and custom
 * finders for checking duplicate teams within a project.
 */
@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {

    /**
     * Finds a team by its name and its associated project.
     * Used to check for duplicate team names within a single project.
     *
     * @param name The name of the team.
     * @param project The Project entity to check against.
     * @return an {@link Optional} containing the team if a match is found.
     */
    Optional<Team> findByNameAndProject(String name, Project project);

    /**
     * Finds a team by its group number and its associated project.
     * Used to check for duplicate group numbers within a single project.
     *
     * @param groupNumber The group number of the team.
     * @param project The Project entity to check against.
     * @return an {@link Optional} containing the team if a match is found.
     */
    Optional<Team> findByGroupNumberAndProject(int groupNumber, Project project);

    /**
     * Finds all teams associated with a specific project.
     *
     * @param projectId The ID of the project.
     * @return a list of {@link Team} entities associated with the project.
     */
    List<Team> findByProjectId(Long projectId);

    /**
     * Finds all teams belonging to a specific course (via Project).
     * 
     * @param courseId The ID of the course.
     * @return List of Teams.
     */
    @Query("SELECT t FROM Team t " +
           "JOIN t.project p " +
           "WHERE p.course.id = :courseId")
    List<Team> findTeamsByCourseId(@Param("courseId") Long courseId);
}