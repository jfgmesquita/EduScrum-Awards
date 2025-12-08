package com.group7.eduscrum_awards.repository;

import com.group7.eduscrum_awards.dto.RankingItemDTO;
import com.group7.eduscrum_awards.model.AwardAssignment;

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

       /**
        * Finds all AwardAssignments for a given student, ordered by assignment date descending.
        * 
        * @param studentId the ID of the student
        * @return list of AwardAssignments for the student
        */
       List<AwardAssignment> findAllByStudentIdOrderByAssignedAtDesc(Long studentId);
}