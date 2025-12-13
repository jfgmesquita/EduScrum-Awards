package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.dto.RankingItemDTO;
import com.group7.eduscrum_awards.model.AwardAssignment;
import com.group7.eduscrum_awards.repository.projection.StudentScoreSummary;
import com.group7.eduscrum_awards.repository.projection.TeamScoreSummary;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for {@link AwardAssignment} entities.
 *
 * Extends Spring Data JPA's {@link JpaRepository} to provide basic CRUD
 * operations for AwardAssignment entities.
 */
@Repository
public interface AwardAssignmentRepository extends JpaRepository<AwardAssignment, Long> {

    /**
     * (1) Finds student rankings by degree ID, including students with 0 points.
     * 
     * @param degreeId the ID of the degree
     * @return list of StudentScoreSummary projections
     */
    @Query("SELECT s.id as studentId, s.name as studentName, COALESCE(SUM(a.points), 0) as totalScore " +
           "FROM Student s " +
           "LEFT JOIN s.awardAssignments aa " +
           "LEFT JOIN aa.award a " +
           "WHERE s.degree.id = :degreeId " + 
           "GROUP BY s.id, s.name " +
           "ORDER BY totalScore DESC")
    List<StudentScoreSummary> findStudentRankingsByDegreeId(@Param("degreeId") Long degreeId);


    /**
     * (2) Finds team rankings by course ID, including teams with 0 points.
     * 
     * @param courseId the ID of the course
     * @return list of TeamScoreSummary projections
     */
    @Query("SELECT t.id as teamId, t.name as teamName, COALESCE(SUM(a.points), 0) as totalScore " +
           "FROM Team t " +
           "JOIN t.project p " +
           "JOIN p.course c " +
           "LEFT JOIN t.members m " +
           "LEFT JOIN AwardAssignment aa ON aa.team = t " +
           "LEFT JOIN aa.award a " +
           "WHERE c.id = :courseId " +
           "GROUP BY t.id, t.name " +
           "ORDER BY totalScore DESC")
    List<TeamScoreSummary> findTeamRankingsByCourseId(@Param("courseId") Long courseId);


    /**
     * (3) Finds team member scores by team ID, including members with 0 points.
     * 
     * @param teamId the ID of the team
     * @return list of StudentScoreSummary projections
     */
    @Query("SELECT s.id as studentId, s.name as studentName, COALESCE(SUM(a.points), 0) as totalScore " +
           "FROM TeamMember tm " +
           "JOIN tm.student s " +
           "LEFT JOIN AwardAssignment aa ON aa.student = s AND aa.team.id = :teamId " + // Prémios ganhos NESTA equipa
           "LEFT JOIN aa.award a " +
           "WHERE tm.team.id = :teamId " +
           "GROUP BY s.id, s.name " +
           "ORDER BY totalScore DESC")
    List<StudentScoreSummary> findTeamMemberScores(@Param("teamId") Long teamId);

    /**
    * Finds all AwardAssignments for a given student, ordered by assignment date descending.
    * 
    * @param studentId the ID of the student
    * @return list of AwardAssignments for the student
    */
    List<AwardAssignment> findAllByStudentIdOrderByAssignedAtDesc(Long studentId);

    /**
     * Finds global student rankings based on total award points.
     * 
     * @return list of StudentScoreSummary projections
     */
    @Query("SELECT s.id as studentId, s.name as studentName, COALESCE(SUM(a.points), 0) as totalScore " +
        "FROM AwardAssignment aa " +
        "JOIN aa.student s " +
        "JOIN aa.award a " +
        "GROUP BY s.id, s.name " +
        "ORDER BY totalScore DESC")
    List<StudentScoreSummary> findGlobalStudentRankings();

    /**
     * Finds student scores within a specific team based on total award points.
     * 
     * @param teamId the ID of the team
     * @return list of StudentScoreSummary projections
     */
    @Query("SELECT s.id as studentId, s.name as studentName, COALESCE(SUM(a.points), 0) as totalScore " +
           "FROM AwardAssignment aa " +
           "JOIN aa.student s " +
           "JOIN aa.award a " +
           "WHERE aa.team.id = :teamId " +
           "GROUP BY s.id, s.name " +
           "ORDER BY totalScore DESC")
    List<StudentScoreSummary> findStudentScoresByTeamId(@Param("teamId") Long teamId);

    /**
    * Individual Student Ranking (By Degree).
    * Sums only the points where 'student' is not null (individual awards).
    */
    @Query("SELECT new com.group7.eduscrum_awards.dto.RankingItemDTO(" +
                    "   s.name, CAST(SUM(aw.points) AS double) ) " +
                    "FROM AwardAssignment aa " +
                    "JOIN aa.student s " +
                    "JOIN aa.award aw " +
                    "WHERE s.degree.id = :degreeId " +
                    "GROUP BY s.id, s.name " +
                    "ORDER BY SUM(aw.points) DESC")
    List<RankingItemDTO> findStudentRankingByDegree(@Param("degreeId") Long degreeId);

    /**
    * Team Ranking (By Course).
    * Sums only the points where 'team' is not null (team awards),
    * and divides the total points by the number of team members.
    */
    @Query("SELECT new com.group7.eduscrum_awards.dto.RankingItemDTO(" +
                    "   t.name, " +
                    "   (SUM(aw.points) * 1.0) / SIZE(t.members) ) " +
                    "FROM AwardAssignment aa " +
                    "JOIN aa.team t " +
                    "JOIN t.project p " +
                    "JOIN aa.award aw " +
                    "WHERE p.course.id = :courseId " +
                    "AND aa.team IS NOT NULL " +
                    "GROUP BY t.id, t.name " +
                    "ORDER BY 2 DESC")
    List<RankingItemDTO> findTeamRankingByCourse(@Param("courseId") Long courseId);
}