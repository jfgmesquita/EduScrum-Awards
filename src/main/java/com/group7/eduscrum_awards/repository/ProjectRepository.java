package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.dto.ProjectCourseTeamsDTO;
import com.group7.eduscrum_awards.dto.teacher.ProjectSummaryDTO;
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
 * Extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD
 * operations and custom finders.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    /**
     * Finds a project by its name and its associated course.
     * Used to check for duplicate project names within a single course.
     *
     * @param name   The name of the project.
     * @param course The Course entity to check against.
     * @return an {@link Optional} containing the project if a match is found.
     */
    Optional<Project> findByNameAndCourse(String name, Course course);

    /**
     * Finds all projects associated with a specific student.
     * This method joins the Project, Team, and TeamMember entities to filter
     * projects
     * where the specified student is a member of the team.
     *
     * @param studentId The ID of the student.
     * @return a list of {@link Project} entities associated with the student.
     */
    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN FETCH p.sprints s " +
            "LEFT JOIN FETCH s.tasks t " +
            "JOIN p.teams tm " +
            "JOIN tm.members m " +
            "WHERE m.student.id = :studentId")
    List<Project> findProjectsByStudentId(@Param("studentId") Long studentId);

    /**
     * Retrieves a summary of projects for a given course, including the number of
     * teams in each project.
     *
     * @param courseId The ID of the course.
     * @return a list of {@link ProjectSummaryDTO} containing project summaries.
     */
    @Query("SELECT new com.group7.eduscrum_awards.dto.teacher.ProjectSummaryDTO(" +
            "p.id, p.name, p.startDate, p.endDate, COUNT(t)) " +
            "FROM Project p LEFT JOIN p.teams t " +
            "WHERE p.course.id = :courseId " +
            "GROUP BY p.id, p.name, p.startDate, p.endDate")
    List<ProjectSummaryDTO> findProjectsWithTeamCount(@Param("courseId") Long courseId);

    /**
     * Finds all projects belonging to courses taught by a specific teacher.
     * 
     * @param teacherId The ID of the teacher.
     * @return List of ProjectSummaryDTO.
     */
    @Query("SELECT new com.group7.eduscrum_awards.dto.teacher.ProjectSummaryDTO(" +
            "p.id, p.name, p.startDate, p.endDate, COUNT(t)) " +
            "FROM Project p " +
            "JOIN p.course c " +
            "JOIN c.teachers tea " +
            "LEFT JOIN p.teams t " +
            "WHERE tea.id = :teacherId " +
            "GROUP BY p.id, p.name, p.startDate, p.endDate")
    List<ProjectSummaryDTO> findProjectsByTeacherId(@Param("teacherId") Long teacherId);

    /**
     * Fetches a project with its Sprints and Tasks eagerly to avoid N+1 issues.
     * Note: Teams are NOT fetched here to avoid Cartesian Product performance hits.
     * 
     * @param projectId The ID of the project.
     * @return Optional Project with sprints and tasks initialized.
     */
    @Query("SELECT DISTINCT p FROM Project p " +
            "LEFT JOIN FETCH p.sprints s " +
            "LEFT JOIN FETCH s.tasks t " +
            "WHERE p.id = :projectId " +
            "ORDER BY s.sprintNumber ASC")
    Optional<Project> findProjectWithSprintsAndTasks(@Param("projectId") Long projectId);

    /**
     * Counts the number of projects in a specific course.
     * 
     * @param courseId The ID of the course.
     * @return The number of projects.
     */
    long countByCourseId(Long courseId);

    /**
     * Retrieves the course name and number of teams for a specific project.
     *
     * @param projectId The ID of the project.
     * @return an {@link Optional} containing {@link ProjectCourseTeamsDTO} if found.
     */
    @Query("SELECT new com.group7.eduscrum_awards.dto.ProjectCourseTeamsDTO(c.name, COUNT(t)) " +
            "FROM Project p " +
            "JOIN p.course c " +
            "LEFT JOIN p.teams t " +
            "WHERE p.id = :projectId " +
            "GROUP BY c.name")
    Optional<ProjectCourseTeamsDTO> findCourseNameAndTeamCountByProjectId(@Param("projectId") Long projectId);
}