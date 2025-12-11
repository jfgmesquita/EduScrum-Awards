package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.model.Course;
import com.group7.eduscrum_awards.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
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

    /**
     * Finds all projects associated with a specific student.
     * This method joins the Project, Team, and TeamMember entities to filter projects
     * where the specified student is a member of the team.
     *
     * @param studentId The ID of the student.
     * @return a list of {@link Project} entities associated with the student.
     */
    @Query("SELECT DISTINCT p FROM Project p " +
           "JOIN p.teams t " +
           "JOIN t.members tm " +
           "WHERE tm.student.id = :studentId")
    List<Project> findProjectsByStudentId(@Param("studentId") Long studentId);
}