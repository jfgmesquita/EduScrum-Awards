package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.model.Project;
import com.group7.eduscrum_awards.model.Student;
import com.group7.eduscrum_awards.model.TeamMember;
import com.group7.eduscrum_awards.model.enums.TaskStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/** Repository for the TeamMember association entity. */
@Repository
public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    /**
     * Checks if a specific student is already a member of any team within a specific project.
     * This enforces the "one team per student per project" rule.
     *
     * @param student The student to check.
     * @param project The project to check within.
     * @return An Optional<TeamMember> if a membership exists.
     */
    Optional<TeamMember> findByStudentAndProject(Student student, Project project);

    /**
     * Retrieves all TeamMember entries for a specific student.
     *
     * @param studentId The ID of the student.
     * @return A list of TeamMember entries associated with the student.
     */
    List<TeamMember> findByStudentId(Long studentId);

    /**
     * Counts tasks with a specific status assigned to teams that the student is a member of.
     *
     * @param studentId The ID of the student.
     * @param status The status of the tasks to count.
     * @return The count of tasks with the specified status.
     */
    @Query("SELECT COUNT(t) FROM Task t " +
           "JOIN t.sprint s " +
           "JOIN s.project p " +
           "JOIN p.teams team " +
           "JOIN team.members m " +
           "WHERE m.student.id = :studentId " +
           "AND t.status = :status")
    long countTasksByStudentTeamsAndStatus(@Param("studentId") Long studentId, @Param("status") TaskStatus status);

    /**
     * Counts all tasks assigned to teams that the student is a member of.
     *
     * @param studentId The ID of the student.
     * @return The total count of tasks.
     */
    @Query("SELECT COUNT(t) FROM Task t " +
           "JOIN t.sprint s " +
           "JOIN s.project p " +
           "JOIN p.teams team " +
           "JOIN team.members m " +
           "WHERE m.student.id = :studentId")
    long countAllTasksByStudentTeams(@Param("studentId") Long studentId);
}